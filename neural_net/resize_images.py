import cv2
import argparse
import os


ap = argparse.ArgumentParser()
ap.add_argument('--dir', required=True, type=str, help='directory to images')
ap.add_argument('--row', required=True, type=int, help="number of rows")
ap.add_argument('--col', required=True, type=int, help="number of columns")
args = ap.parse_args()

for(dirpath, dirnames, filenames) in os.walk(args.dir):
    for f in filenames:
        img_path = dirpath + "/" + f
        # print(img_path)
        img = cv2.imread(img_path)
        resized_image = cv2.resize(img, (args.row, args.col))
        cv2.imwrite(img_path, resized_image)
        print("resized:", img_path)
