from keras import applications
from keras.preprocessing.image import ImageDataGenerator
from keras import optimizers
from keras.models import Sequential
from keras.layers import Dropout, Flatten, Dense, GlobalAveragePooling2D, InputLayer
from keras import backend as k
from keras.callbacks import ModelCheckpoint, LearningRateScheduler, TensorBoard, EarlyStopping
from collections import Counter
import os
import matplotlib.pyplot as plt
import numpy

img_width, img_height = 224, 224
train_data_dir = "dataset/train"
validation_data_dir = "dataset/val"
#nb_train_samples = 4125
#nb_validation_samples = 466
batch_size = 18
epochs = 15
unfreeze = 4

model = applications.VGG19(weights = "imagenet", include_top=False, input_shape = (img_width, img_height, 3))
model_final = Sequential()
model_final.add(InputLayer(input_shape=(img_width, img_height, 3)))
for layer in model.layers:
    model_final.add(layer)
# Freeze the layers which you don't want to train. Here I am freezing the last 5 layers.
for layer in model.layers[:unfreeze]:
    layer.trainable = False

try:
    os.remove("vgg19_1.h5")
except: print("Warning file not found")

#Adding custom Layers
model_final.add(Flatten())
model_final.add(Dense(512, activation='relu'))
model_final.add(Dropout(0.10))
model_final.add(Dense(512, activation='relu'))
model_final.add(Dense(2, activation='softmax'))

# compile the model
model_final.compile(loss = "binary_crossentropy", optimizer = optimizers.SGD(lr=0.0001, momentum=0.9), metrics=["accuracy"])

# Initiate the train and test generators with data Augumentation
train_datagen = ImageDataGenerator(
rescale = 1./255,
horizontal_flip = True,
fill_mode = "nearest",
zoom_range = 0.3,
width_shift_range = 0.3,
height_shift_range=0.3,
rotation_range=30)

test_datagen = ImageDataGenerator(
rescale = 1./255,
horizontal_flip = True,
fill_mode = "nearest",
zoom_range = 0.3,
width_shift_range = 0.3,
height_shift_range=0.3,
rotation_range=30)

train_generator = train_datagen.flow_from_directory(
train_data_dir,
target_size = (img_height, img_width),
batch_size = batch_size,
classes=['trash', 'recycle'])

validation_generator = test_datagen.flow_from_directory(
validation_data_dir,
target_size = (img_height, img_width),
classes=['trash', 'recycle'])

# Save the model according to the conditions
checkpoint = ModelCheckpoint("vgg19_1.h5", monitor='val_acc', verbose=1, save_best_only=True, save_weights_only=False, mode='auto', period=1)
early = EarlyStopping(monitor='val_acc', min_delta=0, patience=5, verbose=1, mode='auto')

counter = Counter(train_generator.classes)
max_val = float(max(counter.values()))
class_weights = {class_id : max_val/num_images for class_id, num_images in counter.items()}

# Train the model
history = model_final.fit_generator(
train_generator,
epochs = epochs,
validation_data = validation_generator,
callbacks = [checkpoint, early],
class_weight=class_weights)

# summarize history for accuracy
plt.plot(history.history['acc'])
plt.plot(history.history['val_acc'])
plt.title('model accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='upper left')
plt.savefig('accuracy.png')
# summarize history for loss
plt.plot(history.history['loss'])
plt.plot(history.history['val_loss'])
plt.title('model loss')
plt.ylabel('loss')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='upper left')
plt.savefig('loss.png')


val_acc = float(history.history["val_acc"][-1])
val_acc = "{0:.3f}".format(val_acc)
save_label = '_e' + str(epochs) + "_va" + str(val_acc)
save_label = save_label + '_unfr' + str(unfreeze)
save_label = "model19" + save_label + ".h5"
print("Saving model to", save_label)
model_final.save(save_label)
