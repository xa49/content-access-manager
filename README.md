This application can be used to serve files stored on our server in a controlled manner:

* Each file is accessible at a dynamically rotating endpoint with the format `/<file-identifier>/<rotating-UUID>` (
  default rotation is 30 seconds)
* Authorized users can get the actual and next UUID by calling `GET /<file-identifier>` and authenticating themselves
  with HTTP Basic
* An admin user can create new users and delete them
* Every access code is valid only for one request

### Testing the app

#### Resource access

The default `username:password` for HTTP Basic is `alice:alice` or you can find the actual access codes printed on
stdout if you set the logging level to DEBUG.

There are two PDF files packaged with the app with identifiers `dummy1` and `dummy2`. There is also a preloaded
identifier (`missing-file`) which does not point to a file in the system. Every other file identifier will result in a
400 response code (which is of course a questionable design decision because then the identifier of every resource can
be determined by brute force).

To view the dummy PDF files returned for your request, use Postman and click the downward arrow next to 'Send', choose '
Send and Download' and save the file as .pdf.

#### User management

The admin user can authenticate itself with `admin:admin` via HTTP Basic. They can make a `POST /admin/user` request with `x-username` and `x-password` details to add a new user. A `DELETE /admin/user/<username>` request removes the user.