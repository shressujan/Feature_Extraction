# Necessary import
from IPython.display import display
import pandas as pd


def read_csv_file(file):
    df = pd.read_csv(file)
    display(df)


if __name__ == '__main__':

    read_csv_file('/Users/sujanshrestha/IdeaProjects/Feature_extraction/src/main/java/feature_csv_files/customerOrderObjectModel.csv')


