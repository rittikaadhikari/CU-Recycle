from flask import Flask, render_template, request, jsonify
import keras
from keras.models import load_model
from keras import optimizers
import argparse
import os
import cv2
import numpy as np
import tensorflow as tf

app = Flask(__name__)

global graph
graph = tf.get_default_graph()
model = load_model('../neural_net/official_model.h5')

# GET/POST
@app.route('/image/<image>', methods=['GET','POST'])
def get_recycle_prediction(image):
    if request.method == 'POST' and 'upload' in request.files:
        file_ = request.files['upload']
        file_.save('image.jpg')
        img = cv2.imread('image.jpg')
        img = cv2.resize(img, (224, 224))
        cv2.imwrite('image.jpg', img)
        img = np.expand_dims(img, axis=0)
        with graph.as_default():
            prediction = model.predict(img, batch_size=1, verbose=1)
            if (prediction[0][0] > prediction[0][1]):
                return "trash"
            else:
                return "recycle"
    return ""

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
