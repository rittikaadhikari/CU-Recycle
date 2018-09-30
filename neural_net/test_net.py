import keras
from keras.models import load_model
from keras import optimizers
import argparse
import os
import cv2
import numpy as np


ap = argparse.ArgumentParser()
ap.add_argument('--dir', required=True, type=str, help='directory to images')
ap.add_argument('--model', type=str, required=True, help="path to model")
args = ap.parse_args()


model = load_model(args.model)
print("Model loaded.")
total_predictions_count = [0, 0]
inc_predictions_count = [0, 0]
class_index_dict = {"trash":0, "recycle":1}
class_val_dict = {"trash":0.0, "recycle":0.0}
if args.dir[-1] != '/':
    args.dir += '/'
for(dirpath, dirnames, filenames) in os.walk(args.dir):
    for f in filenames:
        class_str = dirpath.split('/')[-1]
        img_path = args.dir + class_str + '/' + f
        img = cv2.imread(img_path)
        img = cv2.resize(img, (224, 224))
        img = np.expand_dims(img, axis=0)
        total_predictions_count[class_index_dict[class_str]] += 1
        prediction = model.predict(img)
        class_val_dict["trash"] = "{0:.3f}".format(prediction[0][0])
        class_val_dict["recycle"] = "{0:.3f}".format(prediction[0][1])
        if class_val_dict[class_str] != max(class_val_dict["trash"], class_val_dict["recycle"]):
            print("Incorrect " + class_str + ": " + f, prediction[0])
            inc_predictions_count[class_index_dict[class_str]] += 1

print("--------")
print("Model:", args.model)
print("Trash Accuracy:", (1 - (inc_predictions_count[0]/total_predictions_count[0])) * 100)
print("Recycling Accuracy:", (1 - (inc_predictions_count[1]/total_predictions_count[1])) * 100)
