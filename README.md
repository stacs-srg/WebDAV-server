# README

## webdav standalone

This is a webdav server implementation.


## How to RUN it

Run the WebDAV_StoreBased_Launcher using following params:
	`-r73e057624a5b5005ab0e35ca45f6fb48ddfa8d5e -p8082 -d/Users/sic2/webdav -ssimone`

Copy HTTP.properties and MIME.properties from webdav/impl/ into the respective folders in targets/

## How to add your own file system implementation

- Create a file system implementation under `uk.ac.standrews.cs.filesystem`
- See example file systems for SOS, ABS-Local, ABS-Store
- Create a new WebDAV_X_Launcher under `uk.ac.standrews.cs.webdav.entrypoints`.
- Initialise the file system from the launcher


## TODO
- Properties bundles should be located in resources/
