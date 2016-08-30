# ginnungagap
Ginnungagap appears as the primordial void in the Norse creation... 
TODO: Figure out whether this really is a suitable name for the longterm preservation component for Cumulus



Installing Cumulus libraries to Linux
--------------------------

* Download the Cumulus zip-file for Linux

* Unzip it, and run the installer (remember sudo)

* Fix the rights of the installation directory (`/usr/local/Cumulus_Java_SDK`)
* * `chmod +xr` to read and execute access for your user

* Make your machine load and use the libraries
* * Go to `/etc/ld.so.conf.d`, create a new file for refering to the cumulus  (e.g. `cumulus-[version].conf`, or just `cumulus.conf`), and write the path to your cumulus library files in the new conf file.
* * e.g. `echo "/usr/local/Cumulus_Java_SDK/lib" > cumulus.conf; sudo mv cumulus.conf /etc/ld.so.conf.d/.`
* * Reload the libraries: `sudo ldconfig -v`

