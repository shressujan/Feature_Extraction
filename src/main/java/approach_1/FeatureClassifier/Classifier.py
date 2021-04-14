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
file_path = '/Users/sujanshrestha/IdeaProjects/Feature_Extraction/src/main/java/approach_1/feature_csv_files/union/features.csv'


def read_csv_file(file):
    print('\n#Reading input csv file:', file, '....: Completed')

    local_df = pd.read_csv(file)
    print('\n# Num of rows', local_df.shape[0])
    print('# Num of columns', local_df.shape[1])

    local_df = local_df.sample(frac=1)
    # display(local_df)

    return local_df


def check_drop_null_values(local_df):
    print('\n#Checking for any null values in Dataset....: Completed')
    if local_df.isna().values.any():
        print('There are null values in approach_2.Feature Set')
        local_df.dropna(inplace=True)

    return local_df


def get_class_distribution(class_):
    print('\n# Getting Class distribution for :', class_, '....:Completed\n')
    print(df.groupby(class_).count())


def feature_label_encoding(local_df):
    print('\n# Encoding text labels in the DataFrame with numerical values....: Completed')
    numerical_vectors = {
        'type': {'Class': 0, 'Association': 1},
        'mapStrategy': {'UnionSubclassStrategy': 0, 'UnionSuperclassStrategy': 1, 'JoinedSubclassStrategy': 2,
                        'OwnAssociationTableStrategy': 3, 'ForeignKeyEmbeddingStrategy': 4}
    }
    local_df = local_df.replace(numerical_vectors)
    # print(local_df.dtypes)
    return local_df


# Findings : mapStrategy is strongly positively correlated to the type,  and strongly negatively correlated to num of
# id, num of attributes, num of toManyRelation
def find_correlation_with_mapStrategy(local_df):
    print('\n# Printing Correlation of all features with {mapStrategy}....: Completed')
    print(local_df.corr()['mapStrategy'].sort_values(ascending=False))


def get_input_output_features(local_df):
    print('# Getting Input and output features for given DataFrame....: Completed')
    input_feature = local_df.drop('paretoFrontier', axis=1)
    output_feature = local_df['paretoFrontier']

    return input_feature, output_feature


def standardize_data(train_feature):
    print('# Standardizing the Training feature sets....: Completed')
    scaler = StandardScaler()
    Xs = scaler.fit_transform(train_feature)
    return Xs


def interpret_confusion_matrix(cnf_matrix):
    FP = cnf_matrix.sum(axis=0) - np.diag(cnf_matrix)
    FN = cnf_matrix.sum(axis=1) - np.diag(cnf_matrix)
    TP = np.diag(cnf_matrix)
    TN = cnf_matrix.sum() - (FP + FN + TP)

    FP = FP.astype(float)
    FN = FN.astype(float)
    TP = TP.astype(float)
    TN = TN.astype(float)

    TPR = TP / (TP + FN)
    TNR = TN / (TN + FP)
    FPR = FP / (FP + TN)
    FNR = FN / (TP + FN)

    return TPR, TNR, FPR, FNR


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
    cnf_matrix = confusion_matrix(y_test, y_test_pred)
    TPR, TNR, FPR, FNR = interpret_confusion_matrix(cnf_matrix)

    print('Best param:', knn_cv.best_params_)
    print('Best score:', knn_cv.best_score_)
    print('Test score:', knn_cv.score(X_test, y_test))
    print('Confusion matrix:')
    print(cnf_matrix)
    print('True Positive Rate:', TPR)
    print('True Negative Rate:', TNR)
    print('False Positive Rate:', FPR)
    print('False Negative Rate:', FNR)


def multinomial_naive_bayes_classifier():
    print('\n##   Running MultinomialNB Classifier   ##')
    mnb = Pipeline([('clf', MultinomialNB())])
    param_grid = {
        'clf__alpha': [0.001, 0.1, 1.0, 1.5, 2.0],
    }

    mnb_cv = GridSearchCV(mnb, param_grid, scoring='accuracy', cv=5, verbose=1)
    mnb_cv.fit(X_train, y_train)
    y_test_pred = mnb_cv.predict(X_test)

    print('Best param:', mnb_cv.best_params_)
    print('Best score:', mnb_cv.best_score_)
    print('Test score:', mnb_cv.score(X_test, y_test))
    print('Confusion matrix:')
    print(confusion_matrix(y_test, y_test_pred))


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
    get_class_distribution('paretoFrontier')
    get_class_distribution('type')
    get_class_distribution('mapStrategy')

    df = feature_label_encoding(df)
    find_correlation_with_mapStrategy(df)
    X, y = get_input_output_features(df)

    # Split the data in training and testing dataset
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    print('\nX_train shape:', X_train.shape)

    # X_train = standardize_data(X_train)
    # X_test = standardize_data(X_test)

    # Calling KNearestNeighborClassifier
    k_nearest_neighbors_classifier()

    # Calling Multinomial Naive Bayes Classifier
    # multinomial_naive_bayes_classifier()

    # Calling Linear Support Vector Classifier
    # linear_support_vector_classifier()



