## Bluetooth HID Emulator (Android)
[![DOI](https://zenodo.org/badge/9749636.svg)](https://zenodo.org/badge/latestdoi/9749636)
====================

#### BRIEF HISTORY

This project started out as a personal endeavour to use my smartphone as a controller / keyboard for a PS3. The intent was not to actually use it as a controller to play games, but instead to type text messages or perform light navigation on the PS3, playback music / videos, etc.

Unsurprisingly, actually get it working on android presented one major challenge: There was no easy way to provide an HID profile through Android API.

#### ANDROID VERSION

Code is written for gingerbread, probably will need some rework for a newer API levels.

#### CAVEATS

Due to the limitation of bluetooth android API (again, gingerbread perspective), There are two "routes" in order to be able to add the required PS3 HID profile:

* *Rooted device:* A native binary will be installed along with the app, used to add the PS3 HID profile.
* *Custom-build android framework:* Suggested patches are provided under `android_framework_patches` to add the necessary API calls to the framework and bluez implementation.

Ugly, but functional. Both methods worked very well with a couple of motorola handsets where this was tested - surely this needs to be evaluated on other devices. Also, I didn't had the time to port or check newer android API levels, so there could possibly be cleaner alternatives nowdays.

#### LICENSE

Except as otherwise noted, Bluetooth HID Emulator is licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html).

-- *Augusto Andraus*
