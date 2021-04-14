# Necessary import
from IPython.display import display
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split, GridSearchCV, cross_val_score, cross_val_predict
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

# Importing Sklearn Classifiers
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import MultinomialNB
from sklearn.svm import LinearSVC
from sklearn.metrics import confusion_matrix

pd.options.display.max_columns = None
pd.set_option('expand_frame_repr', False)

# Only works when absolute path is given!
file_path = '/Users/sujanshrestha/IdeaProjects/Feature_Extraction/src/main/java/approach_2/feature_csv_files/union/features.csv'


def read_csv_file(file):
    print('\n#Reading input csv file:', file, '....: Completed')

    local_df = pd.read_csv(file)
    print('\n# Num of rows', local_df.shape[0])
    print('# Num of columns', local_df.shape[1])

    local_df = local_df.sample(frac=1)

    local_df.reset_index(drop=True, inplace=True)
    display(local_df)

    return local_df


def check_drop_null_values(local_df):
    print('\n#Checking for any null values in Dataset....: Completed')
    if local_df.isna().values.any():
        print('There are null values in Feature Set')
        local_df.dropna(inplace=True)

    return local_df


def get_class_distribution(class_):
    print('\n# Getting Class distribution for :', class_, '....:Completed\n')
    print(df.groupby(class_).count())


def feature_label_encoding(local_df):
    print('\n# Encoding text labels in the DataFrame with numerical values....: Completed')
    numerical_vectors = {'association': {'none': 0, 'OwnAssociationTableStrategy': 1, 'ForeignKeyEmbeddingStrategy': 2},
                         'mappingStrategy': {'UnionSubclassStrategy': 0, 'UnionSuperclassStrategy': 1,
                                             'JoinedSubclassStrategy': 2}
                         }
    local_df = local_df.replace(numerical_vectors)
    # print(local_df.dtypes)
    return local_df


# Findings : mapStrategy is strongly positively correlated to the type,  and strongly negatively correlated to num of
# id, num of attributes, num of toManyRelation
def find_correlation_with_mapStrategy(local_df):
    print('\n# Printing Correlation of all features with {paretoOptimal}....: Completed')
    print(local_df.corr()['paretoOptimal'].sort_values(ascending=False))


def get_input_output_features(local_df):
    print('\n# Getting Input and output features for given DataFrame....: Completed')
    input_feature = local_df.drop(['object', 'hasId', 'isSrcMultiplicity', 'paretoOptimal'], axis=1)
    output_feature = local_df['paretoOptimal']

    return input_feature, output_feature


def standardize_data(train_feature):
    print('# Standardizing the Training feature sets....: Completed')
    scaler = StandardScaler()
    Xs = scaler.fit_transform(train_feature)
    return Xs


def helper_function(y_true, y_pred):
    TP = 0
    TN = 0
    FP = 0
    FN = 0
    for i in range(len(y_true)):
        if y_true[i] == 1 and y_pred[i] == 1:
            TP += 1
        elif y_true[i] == 1 and y_pred[i] == 0:
            FN += 1
        elif y_true[i] == 0 and y_pred[i] == 0:
            TN += 1
        elif y_true[i] == 0 and y_pred[i] == 1:
            FP += 1
    return TP, TN, FP, FN  # True Positive, True Negative, False Positive, False Negative


def receiver_op_curve(y_true, y_pareto_optimal_probability_scores):
    threshold = [x * .05 for x in range(25)]
    TPR = []
    FPR = []

    paretoFrontier = 1
    notParetoFrontier = 0

    for th in threshold:
        new_y_pred = y_pareto_optimal_probability_scores[:]
        for i in range(len(new_y_pred)):
            if new_y_pred[i] >= th:
                new_y_pred[i] = paretoFrontier
            else:
                new_y_pred[i] = notParetoFrontier

        TP, TN, FP, FN = helper_function(y_true, new_y_pred)
        if TP == 0 and FN == 0:
            TPR.append(0)
        else:
            TPR.append(TP / (TP + FN))  # True Positive Rate

        if FP == 0 and TN == 0:
            FPR.append(0)
        else:
            FPR.append(FP / (FP + TN))  # False Positive

    return FPR, TPR, threshold


def k_nearest_neighbors_classifier():
    print('\n##   Running KNeighborClassifier   ##')
    k_range = list(range(1, 5))
    print('# Range of Neighbors:', k_range)
    param_grid = {'n_neighbors': k_range, 'weights': ('uniform', 'distance')}
    knn = KNeighborsClassifier()

    # test
    # scores = cross_val_score(knn, X_train, y_train, cv=10, scoring='accuracy')
    # print(scores.mean())

    knn_cv = GridSearchCV(knn, param_grid, cv=5, scoring='accuracy')
    knn_cv.fit(X_train, y_train)
    y_test_pred = knn_cv.predict(X_test)
    y_test_pred_prob = knn_cv.predict_proba(X_test)
    cnf_matrix = confusion_matrix(y_test, y_test_pred)

    FPR, TPR, threshold = receiver_op_curve(y_test, y_test_pred_prob[:, 1])

    print('Best param:', knn_cv.best_params_)
    print('Best score:', knn_cv.best_score_)
    print('Test score:', knn_cv.score(X_test, y_test))
    print('Confusion matrix:')
    print(cnf_matrix)
    print(FPR, TPR, threshold)


def multinomial_naive_bayes_classifier():
    print('\n##   Running MultinomialNB Classifier   ##')
    mnb = Pipeline([('clf', MultinomialNB())])
    param_grid = {
        'clf__alpha': [0.001, 0.1, 1.0, 1.5, 2.0],
    }

    mnb_cv = GridSearchCV(mnb, param_grid, scoring='accuracy', cv=5, verbose=1)
    mnb_cv.fit(X_train, y_train)
    y_test_pred = mnb_cv.predict(X_test)
    y_test_pred_prob = mnb_cv.predict_proba(X_test)

    FPR, TPR, threshold = receiver_op_curve(y_test.to_numpy(), y_test_pred_prob[:, 1])

    print('Best param:', mnb_cv.best_params_)
    print('Best score:', mnb_cv.best_score_)
    print('Test score:', mnb_cv.score(X_test, y_test))
    print('Confusion matrix:')
    print(confusion_matrix(y_test, y_test_pred))

    for i in range(len(FPR)):
        print(FPR[i], TPR[i], threshold[i])


def linear_support_vector_classifier():
    print('\n##   Running LinearSV Classifier   ##')
    param_grid = {'C': [1000, 500, 100, 10, 1]}
    svc = LinearSVC()
    svc_cv = GridSearchCV(svc, param_grid, scoring='accuracy', cv=5, verbose=3)
    svc_cv.fit(X_train, y_train)
    y_test_pred = svc_cv.predict(X_test)

    print('Best param:', svc_cv.best_params_)
    print('Best score:', svc_cv.best_score_)
    print('Test score:', svc_cv.score(X_test, y_test))
    print('Confusion matrix:')
    print(confusion_matrix(y_test, y_test_pred))


if __name__ == '__main__':
    df = read_csv_file(file_path)
    df = check_drop_null_values(df)

    # Gets all the distribution by input class_ parameter
    get_class_distribution('paretoOptimal')
    get_class_distribution('mappingStrategy')
    get_class_distribution('association')

    df = feature_label_encoding(df)
    find_correlation_with_mapStrategy(df)
    X, y = get_input_output_features(df)

    # Split the data in training and testing dataset
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)
    print('\nX_train shape:', X_train.shape)

    # X_train = standardize_data(X_train)
    # X_test = standardize_data(X_test)

    # Calling KNearestNeighborClassifier
    # k_nearest_neighbors_classifier()

    # Calling Multinomial Naive Bayes Classifier
    multinomial_naive_bayes_classifier()

    # Calling Linear Support Vector Classifier
    # linear_support_vector_classifier()
