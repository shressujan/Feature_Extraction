# Necessary import
from IPython.display import display
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split, GridSearchCV, cross_val_score, cross_val_predict
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

# Importing Sklearn Classifiers
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import LinearSVC
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix, roc_auc_score, precision_score, recall_score, f1_score, accuracy_score, \
    classification_report
from sklearn.utils import resample
from sklearn.ensemble import RandomForestClassifier

from imblearn.over_sampling import SMOTE
from sklearn.decomposition import PCA
import warnings
from timeit import default_timer as timer

pd.options.display.max_columns = None
pd.set_option('expand_frame_repr', False)

# Only works when absolute path is given!
file_path = '/Users/sujanshrestha/IdeaProjects/Feature_Extraction/src/main/java/approach_2/feature_csv_files/union/features.csv'


def read_csv_file(file):
    print('\n#Reading input csv file:', file, '....: Completed')

    local_df = pd.read_csv(file)
    print('\n#Before resampling')
    print(local_df.paretoOptimal.value_counts())

    local_df = upsample_dataset(local_df)

    print(local_df.paretoOptimal.value_counts())

    local_df = local_df.sample(frac=1)
    local_df.reset_index(drop=True, inplace=True)
    display(local_df)

    return local_df


def upsample_SMOTE_dataset(x_train, y_train):
    sm = SMOTE(random_state=2)
    return sm.fit_resample(x_train, y_train.ravel())


def upsample_dataset(local_df):
    df_majority = local_df[local_df.paretoOptimal == 0]
    df_minority = local_df[local_df.paretoOptimal == 1]

    # Upsampled minority class
    df_minority_upsampled = resample(df_minority,
                                     replace=True,
                                     n_samples=len(df_majority),
                                     random_state=28)

    local_df = pd.concat([df_majority, df_minority_upsampled])

    return local_df


def downsample_dataset(local_df):
    df_majority = local_df[local_df.paretoOptimal == 0]
    df_minority = local_df[local_df.paretoOptimal == 1]

    # Downsampled majority class
    df_majority_downsampled = resample(df_majority,
                                       replace=True,
                                       n_samples=33,
                                       random_state=28)
    local_df = pd.concat([df_majority_downsampled, df_minority])

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

    # Get all the unique associations in the DataFrame
    associations = local_df.associations.unique()
    asc_numeric_dict = dict((asc, index) for index, asc in enumerate(associations))
    # print(asc_numeric_dict)

    numerical_vectors = {
        'associations': asc_numeric_dict,
        'mappingStrategy': {'UnionSubclassStrategy': 0, 'UnionSuperclassStrategy': 1,
                            'JoinedSubclassStrategy': 2},
        'parentMappingStrategy': {'none': 0, 'UnionSubclassStrategy': 1, 'UnionSuperclassStrategy': 2,
                                  'JoinedSubclassStrategy': 3}
    }
    local_df = local_df.replace(numerical_vectors)
    # print(local_df)
    return local_df


# Findings : mapStrategy is strongly positively correlated to the type,  and strongly negatively correlated to num of
# id, num of attributes, num of toManyRelation
def find_correlation_with_mapStrategy(local_df):
    print('\n# Printing Correlation of all features with {paretoOptimal}....: Completed')
    print(local_df.corr()['paretoOptimal'].sort_values(ascending=False))


def get_input_output_features(local_df):
    print('\n# Getting Input and output features for given DataFrame....: Completed')
    input_feature = local_df.drop(['object', 'hasId', 'srcOneToRelation', 'dstToOneRelation', 'paretoOptimal'], axis=1)
    output_feature = local_df['paretoOptimal']

    return input_feature, output_feature


def standardize_data(train_feature, test_feature):
    print('# Standardizing the Training and Testing feature sets....: Completed')
    scaler = StandardScaler()
    train_x = scaler.fit_transform(train_feature)
    test_x = scaler.fit_transform(test_feature)
    return train_x, test_x


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


# Apply PCA reduction technique to reduce the dimension of input features
def apply_PCA_reduction(x):
    pca = PCA(n_components=2)
    pca.fit(x)
    print("\n#Dimensionality reduction technique PCA....: Applied")
    return pca


# Knn Classifier with GridSearchCV to allow k-fold cross validation during model training.
# Use 5 fold cross validation strategy
# Scoring function used is 'f1'
# n_jobs = -1 means use all the available processors in parallel
# Hyper parameter are n_neighbors, weights (type of distance), p
def k_nearest_neighbors_classifier(x_train, x_test):
    warnings.filterwarnings('ignore')
    x_train_std, x_test_std = standardize_data(x_train, x_test)

    print('\n##   Running KNeighborClassifier   ##')
    k_range = list(range(1, 30))
    print('# Range of Neighbors:', k_range)
    param_grid = {'n_neighbors': k_range, 'weights': ('uniform', 'distance'), 'p': [1, 2, 5, 10, 20, 30, 50, 100]}

    knn = KNeighborsClassifier()

    # test
    # scores = cross_val_score(knn, X_train, y_train, cv=10, scoring='accuracy')
    # print(scores.mean())

    start = timer()

    knn_cv = GridSearchCV(knn, param_grid, cv=5, scoring='f1', n_jobs=-1)
    knn_cv.fit(x_train_std, y_train)
    optimal_params = knn_cv.best_params_

    end = timer()

    print('Best param:', optimal_params)
    print('Best score:', knn_cv.best_score_)

    # Training KNN with best parameters
    knn_clf = KNeighborsClassifier(**optimal_params)
    knn_clf.fit(x_train_std, y_train)
    y_test_pred = knn_clf.predict(x_test_std)
    y_test_pred_prob = knn_clf.predict_proba(x_test_std)

    print('Accuracy score:', accuracy_score(y_test, y_test_pred))
    print('ROC_AUC score:', roc_auc_score(y_test, y_test_pred_prob[:, 1]))
    print('Confusion matrix:')
    print('[[TN   FP]')
    print(' [FN   TP]]')
    print(confusion_matrix(y_test, y_test_pred))

    print('\nClassification Report')
    print(classification_report(y_test, y_test_pred))
    print('\n Execution time:', end - start)


# Gaussian naive bayes classifier with GridSearchCV for K fold cross validation during model training.
# No hyper-parameters
# Smoothing function var smoothing, Scoring function used 'f1'
def gaussian_naive_bayes_classifier(x_train, x_test):
    warnings.filterwarnings('ignore')

    print('\n##   Running GaussianNB Classifier   ##')
    gnb = GaussianNB()
    param_grid = {
        'var_smoothing': [0.000000001, 0.00000001, 0.0000001, 0.000001, 0.00001, 0.0001]
    }

    start = timer()

    mnb_cv = GridSearchCV(gnb, param_grid, scoring='f1', cv=5, verbose=3, n_jobs=-1)
    mnb_cv.fit(x_train, y_train)
    optimal_params = mnb_cv.best_params_

    end = timer()

    print('Best param:', optimal_params)
    print('Best score:', mnb_cv.best_score_)

    # Training MultinomialNB with optimal parameters
    mnb_clf = GaussianNB(**optimal_params)
    mnb_clf.fit(x_train, y_train)
    y_test_pred = mnb_clf.predict(x_test)
    y_test_pred_prob = mnb_clf.predict_proba(x_test)

    print('Accuracy score:', accuracy_score(y_test, y_test_pred))
    print('ROC_AUC score:', roc_auc_score(y_test, y_test_pred_prob[:, 1]))
    print('Confusion matrix:')
    print('[[TN   FP]')
    print(' [FN   TP]]')
    print(confusion_matrix(y_test, y_test_pred))

    print('\nClassification Report')
    print(classification_report(y_test, y_test_pred))
    print('\n Execution time:', end - start)


# Linear support vector classifier with GridSearchCV for k fold validation during model training
# Hyper-parameter is the regularization parameter 'C'
# Scoring function is f1, cv= 5 folds
def linear_support_vector_classifier(x_train, x_test):
    warnings.filterwarnings('ignore')
    x_train_std, x_test_std = standardize_data(x_train, x_test)

    print('\n##   Running LinearSV Classifier   ##')
    param_grid = {'C': [1000, 500, 100, 10, 1]}

    start = timer()
    lsv = LinearSVC()
    lsv_cv = GridSearchCV(lsv, param_grid, scoring='f1', cv=5, verbose=1, n_jobs=-1)
    lsv_cv.fit(x_train_std, y_train)
    optimal_params = lsv_cv.best_params_
    print('Best param:', optimal_params)
    print('Best score:', lsv_cv.best_score_)
    end = timer()

    # Training LinearSVC with optimal parameters
    lsv_clf = LinearSVC(**optimal_params)
    lsv_clf.fit(x_train_std, y_train)
    y_test_pred = lsv_clf.predict(x_test_std)

    print('Accuracy score:', accuracy_score(y_test, y_test_pred))
    print('Confusion matrix:')
    print('[[TN   FP]')
    print(' [FN   TP]]')
    print(confusion_matrix(y_test, y_test_pred))

    print('\nClassification Report')
    print(classification_report(y_test, y_test_pred))
    print('\n Execution time:', end - start)


# Logistic Regression classifier with GridSearchCV for k fold cross validation during model training
# Hyper-parameters solver, tol, max_iter, C
# scoring function f1, 5 fold cross validation
def logistic_regression_classifier(x_train, x_test):
    warnings.filterwarnings('ignore')
    x_train_std, x_test_std = standardize_data(x_train, x_test)

    print('\n## Running Logistic Regression Classifier  ##')
    param_grid = {'solver': ['liblinear', 'newton-cg', 'lbfgs'],
                  'tol': [1e-3, 1e-4], 'max_iter': [100, 500, 1000],
                  'C': [0.1, 0.5, 1, 1.5, 2, 50, 100]}

    start = timer()

    lgr = LogisticRegression()
    lgr_cv = GridSearchCV(lgr, param_grid, scoring='f1', cv=5, verbose=1, n_jobs=-1)
    lgr_cv.fit(x_train_std, y_train)
    optimal_params = lgr_cv.best_params_

    end = timer()

    print('Best param:', optimal_params)
    print('Best score:', lgr_cv.best_score_)

    # Training LogisticRegression classifier with optimal parameters
    lgr_clf = LogisticRegression(**optimal_params)
    lgr_clf.fit(x_train_std, y_train)
    y_test_pred = lgr_clf.predict(x_test_std)
    y_test_pred_prob = lgr_clf.predict_proba(x_test_std)

    print('Accuracy score:', accuracy_score(y_test, y_test_pred))
    print('ROC_AUC score:', roc_auc_score(y_test, y_test_pred_prob[:, 1]))
    print('Confusion matrix:')
    print('[[TN   FP]')
    print(' [FN   TP]]')
    print(confusion_matrix(y_test, y_test_pred))

    print('\nClassification Report')
    print(classification_report(y_test, y_test_pred))
    print('\n Execution time:', end - start)


# RandomForestClassifier with GridSearchCV for k fold cross validation during model training.
# Hyper-parameters are n_estimators, max_features, max_depth
# Scoring function f1, 5 fold cross validation
def random_forest_classifier(x_train, x_test):
    warnings.filterwarnings('ignore')
    x_train_std, x_test_std = standardize_data(x_train, x_test)

    print('\n##   Running Random Forest Classifier   ##')
    param_grid = {'n_estimators': [500, 1000],
                  'max_features': ['auto', 'sqrt', 'log2'],
                  'max_depth': [4, 5, 6]}

    start = timer()

    rfc = RandomForestClassifier(random_state=42)
    rfc_cv = GridSearchCV(rfc, param_grid, cv=5, verbose=1, n_jobs=-1)
    rfc_cv.fit(x_train_std, y_train)
    optimal_params = rfc_cv.best_params_

    end = timer()

    print('Best param:', optimal_params)
    print('Best score:', rfc_cv.best_score_)

    # Training RandomForestClassifier with optimal parameters
    rfc_clf = RandomForestClassifier(**optimal_params)
    rfc_clf.fit(x_train_std, y_train)
    y_test_pred = rfc_clf.predict(x_test_std)
    y_test_pred_prob = rfc_clf.predict_proba(x_test_std)

    print('Accuracy score:', accuracy_score(y_test, y_test_pred))
    print('ROC_AUC score:', roc_auc_score(y_test, y_test_pred_prob[:, 1]))
    print('Confusion matrix:')
    print('[[TN   FP]')
    print(' [FN   TP]]')
    print(confusion_matrix(y_test, y_test_pred))

    print('\nClassification Report')
    print(classification_report(y_test, y_test_pred))
    print('\n Execution time:', end - start)


if __name__ == '__main__':
    df = read_csv_file(file_path)
    df = check_drop_null_values(df)

    # Gets all the distribution by input class_ parameter
    get_class_distribution('paretoOptimal')
    get_class_distribution('mappingStrategy')
    get_class_distribution('associations')

    df = feature_label_encoding(df)
    find_correlation_with_mapStrategy(df)
    X, y = get_input_output_features(df)

    # Split the data in training and testing dataset
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=28)
    print('\nX_train shape:', X_train.shape)

    # Calling PCA reduction
    pca_tech = apply_PCA_reduction(X_train)
    X_train_reduced = pca_tech.transform(X_train)
    X_test_reduced = pca_tech.transform(X_test)

    # # Calling KNearestNeighborClassifier with input feature
    # k_nearest_neighbors_classifier(X_train, X_test)
    # # Best param: {'n_neighbors': 25, 'p': 1, 'weights': 'distance'}
    #
    # # Calling KNearestNeighborsClassifier with input features with reduced dimension
    # k_nearest_neighbors_classifier(X_train_reduced, X_test_reduced)
    # # Best param: {'n_neighbors': 27, 'p': 1, 'weights': 'distance'}

    # Calling Gaussian Naive Bayes Classifier
    # gaussian_naive_bayes_classifier(X_train, X_test)
    # Best param: {'alpha': 1e-06}

    # Calling Linear Support Vector Classifier
    linear_support_vector_classifier(X_train, X_test)
    # Best param: {'C': 10}

    # Calling Linear Support Vector Classifier with input features with reduced dimension
    linear_support_vector_classifier(X_train_reduced, X_test_reduced)

    # # Calling Logistic Regression Classifier
    # logistic_regression_classifier(X_train, X_test)
    #
    # # Calling Logistic Regression with input features with reduced dimension
    # logistic_regression_classifier(X_train_reduced, X_test_reduced)

    # # Calling Random Forest Classifier
    # random_forest_classifier(X_train, X_test)
    # # Best param: {'max_depth': 6, 'max_features': 'auto', 'n_estimators': 500}
    # # 1       0.79P      0.72R      0.75F1
    #
    # # Calling Random Forest Classifier with input features with reduced dimension
    # random_forest_classifier(X_train_reduced, X_test_reduced)
    # # Best param: {'max_depth': 6, 'max_features': 'auto', 'n_estimators': 500}
    # # 1       0.77P      0.74R      0.77F1
