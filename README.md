# Q-Tify
## A Song Request Application Linked To Your Spotify Queue

### Why?
If you're hosting a gathering and are playing music via Spotify,
chances are that at some point, someone at the gathering will ask
you to queue up a song for them. For smaller gatherings, this is
a menial task, but for larger gatherings and social settings, it can
get annoying being bombarded by song requests. This app aims to streamline
the "request song -> approve/deny -> add to queue if approved" process, all
while removing the need for direct interactions between the DJ and guests.

### Design
In order to provide a robust application while respecting user privacy,
there are no "Q-Tify accounts". A sign-up process would both add unnecessary
complexity to the project and would act as an annoying barrier to entry for
people who just want to quickly request a song at a party. The less time the
user needs to spend on the app, the better.

As such, a "Host" is simply a user that has linked their Spotify account with
the app. A "Guest" is a user that hasn't. During the linking process,
Spotify user credentials are handled only by Spotify itself, and the only
user data visible to this application is the relevant API access/refresh tokens
along with their Spotify account ID with which the tokens are identified by
internally.

Originally, this project was designed as a REST API with which the Spotify
API could be quickly and easily accessed. As such, designing both a web
frontend and an Android app for this service would be fairly straightforward:
query data from the backend and render it. Decentralizing the backend and
the frontend allowed me to focus on one thing at a time. Making a backend change
to, for example, add a new feature, doesn't affect how things get displayed to
the user. As long as the structure of the data being sent from the backend doesn't
change, I could basically ignore the frontend to focus completely on the backend
development. Similarly, developing the frontend was simply a matter of processing
the JSON data sent from the backend and rendering it; no distractions to worry
about from the Spotify API, credentials, databases, etc.

### Building
First, clone the repository with `git clone https://github.com/bclarke98/q-tify`.

Next, open the `q-tify/source` directory in Android Studio.

When Android Studio opens, wait for the gradle sync to complete and
then click `Build > Make Project`. From here, you can build an
APK (`Build > Build Bundle(s) / APK(s) > Build APK(s)`) or just run
the app directly from Android Studio with either the emulator or an
Android device with USB Debugging enabled.

