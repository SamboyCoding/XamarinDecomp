# XamarinDecomp
XamarinDecomp is a tool to reverse engineer Android APK files compiled using Xamarin.Android enterprise, which obfuscates all the .NET code into a file called `libmonodroid_bundle_app.so` inside the APK (which itself is just a zip file).

It's written in Kotlin, so compiles to a .jar file, and can be run from the command line as `java -jar XamarinDecomp.jar [YourAPK].apk`. Doing so will create a directory called `[YourAPK]_out`, extract the relevant `.so` file, tear it apart to extract the GZipped DLL files, and then un-gzip them. All the files will be placed into the output directory created. 

The DLL files thus created can then be decompiled using any .NET decompiler, of which I would personally recommend dnSpy, simply because it handles certain structures which no other decompiler I've seen can. 

You can get the latest version from the releases tab.
