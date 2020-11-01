# MyMNIST
Mobile app for handwritten digit detection using the MNIST dataset and implemented in TFLite.
Developed on top of the [app by Siraj Raval](https://github.com/llSourcell/A_Guide_to_Running_Tensorflow_Models_on_Android), mainly his UI and drawing API. My focus on the project was to learn to integrate and deploy a Deep Learning model on a mobile app.
The **MNIST_for_TFLite.ipynb** trains a convolutional model and exports it in a **model.tflite** file, which must be dragged into the **assets** folder in the Android project view in Android Studio.
