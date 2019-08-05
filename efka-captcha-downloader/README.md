# efka-captcha-downloader

A simple, pure Java, captcha image downloader. This is the first step to build your own datasets.

## Usage

Just run the application and captcha images will be saved into the `download` folder.

After downloader finished, you will see the `download` folder conains some images

```
2ee43351-a386-44b9-bc6a-fa4545abebdf.jpg  d2fd9ee9-8f51-4e6a-9919-fb5f4bf08f08.jpg
3d5d92eb-c750-469d-8a74-d0bbbac134fc.jpg  df475b28-76e3-40d4-ad6c-3e6ac4903f6e.jpg
7d78192c-287c-49cc-921e-c6501c08a3de.jpg  eb0d1b23-a067-452f-a3a1-31ff105c5ac7.jpg
9915dada-c614-4bda-b13a-a16f11a7876a.jpg  f716ff47-ddd1-450d-bcad-e570aa2f7bc8.jpg
```
Rename these image to their captcha value.

Take `2ee43351-a386-44b9-bc6a-fa4545abebdf.jpg` as example, since it's value is `3cfcc`, you should rename it to `3cfcc.jpg`.
