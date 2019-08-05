# efka-captcha-training
After collect enough captcha images, feed these datasets to this project and get the training model.

## Usage

In `resources`, there are three folders, each one should have different datasets:

- test

  Copy some images from `train` set, this set is mainly to determine whether it's good that the model fited traing data.

- train

  Copy most of your captcha images here, this datasets is used for train model.

- valid

  This is varification set, put the captcha images not in `traing` set to determine the model is good or bad. 

After build your own set, just run application and wait for the training result. You may see result like belows:

```
validate result : sum count = 1346 correct count = 1295
```

Copy the `out/model.zip` to [efka-captcha-solver](https://github.com/g-eleutheriou/efka-captcha-solver-dl4j/blob/master/efka-captcha-solver/README.md)'s resource directory and build your solver.

## UI Server

You can connect to http://localhost:9000 to see the training result
