# efka-captcha-solver-dl4j
A [deeplearning4j](https://deeplearning4j.org/) based captcha training tool for the Greek EFKA service [website](https://www.efka.gov.gr).

This project is a fork of [twse-captcha-solver-dl4j](https://github.com/coldnew/twse-captcha-solver-dl4j), so all credits goes to the owner of the original repository.

-----

## Usage

This project is built with three subprojects:

- [efka-captcha-downloader](https://github.com/g-eleutheriou/efka-captcha-solver-dl4j/blob/master/efka-captcha-downloader/README.md)

  A simple, pure Java, captcha image downloader. This is the first step to build your own datasets.

- [efka-captcha-training](https://github.com/g-eleutheriou/efka-captcha-solver-dl4j/blob/master/efka-captcha-training/README.md)

  After collect enough captcha images, feed these datasets to this project and get the training model.

- [efka-captcha-solver](https://github.com/g-eleutheriou/efka-captcha-solver-dl4j/blob/master/efka-captcha-solver/README.md)

  Use the training model generated from [efka-captcha-training](https://github.com/g-eleutheriou/efka-captcha-solver-dl4j/blob/master/efka-captcha-training/README.md) and create your own captcha solver.

For more info, please see each project's README.

## Example Captcha Image

![example_captcha](https://raw.githubusercontent.com/g-eleutheriou/efka-captcha-solver-dl4j/master/efka-captcha-solver/captcha.jpg)
