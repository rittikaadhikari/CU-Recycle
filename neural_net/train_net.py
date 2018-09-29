import keras
import numpy as np
from keras import backend as K
from keras.models import Sequential
from keras.engine import InputLayer
from keras.layers import Activation
from keras.layers.core import Dense, Flatten, Dropout
from keras.optimizers import Adam
from keras.metrics import binary_crossentropy
from keras.preprocessing.image import ImageDataGenerator
from keras.layers.normalization import BatchNormalization
from keras.layers.convolutional import *
from matplotlib import pyplot as plt
import itertools
import os
from collections import Counter
import argparse
import cv2

train_path = 'dataset/train'
valid_path = 'dataset/val'

ap = argparse.ArgumentParser()
ap.add_argument('--dont_save', action='store_true', help='save model to disk')
ap.add_argument('--verbose', action='store_true', help='verbose output')
ap.add_argument('--epochs', type=int, default=5)
ap.add_argument('--batch_size', type=int, default=15)
ap.add_argument('--freeze', type=int, help="number of layers to freeze, max = 25", default=22)
args = ap.parse_args()

train_datagen = ImageDataGenerator(rotation_range=5,
  width_shift_range=0.2,
  height_shift_range=0.2,
  horizontal_flip=True,
  zoom_range=.25,
  )
train_generator = train_datagen.flow_from_directory(train_path, target_size=(224,224), classes=['trash', 'recycle'], batch_size=args.batch_size)

valid_batches = ImageDataGenerator(horizontal_flip=True).flow_from_directory(valid_path, target_size=(224,224), classes=['trash', 'recycle'], batch_size=29)

#init vgg16 model
vgg16_model = keras.applications.vgg16.VGG16()

#create our model based off of vgg16
model = Sequential()
model.add(InputLayer(input_shape=(224,224,3)))
for layer in vgg16_model.layers[:-1]:
    model.add(layer)

model.add(Dense(1024, activation='relu'))
model.add(Dropout(.5))
model.add(Dense(3, activation='sigmoid'))

num_layers = len(model.layers)
frozen_count = 0
for layer in model.layers:
    if frozen_count < args.freeze:
        layer.trainable = False
        frozen_count += 1

if(args.verbose):
    #DEBUG:look at model
    model.summary()
    print("number of layers="+str(num_layers))
    print("number of frozen="+str(args.freeze))

#compile model with Adam optimization function (learning rate = .0001)
model.compile(Adam(lr=.0001), loss='binary_crossentropy', metrics=['accuracy'])

#for class_weight trash is index 0 and recycle is index 1
#gets the correct class_weights
counter = Counter(train_generator.classes)
max_val = float(max(counter.values()))
class_weights = {class_id : max_val/num_images for class_id, num_images in counter.items()}

steps = 1673 // args.batch_size
history = model.fit_generator(train_generator, steps_per_epoch=steps, validation_data=valid_batches, validation_steps=109, epochs=args.epochs, verbose=1, class_weight=class_weights)

if(not args.dont_save):
    val_acc = float(history.history["val_acc"][-1])
    val_acc = "{0:.3f}".format(val_acc)
    save_label = '_e' + str(args.epochs) + "_va" + str(val_acc)
    save_label = save_label + '_unfr' + str(args.freeze)
    save_label = "model" + save_label + ".h5"
    print("Saving model to", save_label)
    model.save(save_label)
