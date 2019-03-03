# junk-sender

A Clojure library for email templating.

## Install and run

    git clone https://github.com/mladensavic94/junk-sender.git
    cd junk-sender
    lein ring uberjar
    cd target
    java -jar junk-sender-0.1.0-SNAPSHOT-standalone.jar


## Usage

Template is defined by sending POST request with parameters
:tempID :src :params, where tempID must be unique for each template,
src is predefined text (or html) with
placeholders for parameters
and params an array of required parameters for assembling template.

    curl -H "Content-Type: application/json" -X POST http://localhost:3000/api/v1/template
    -d {"tempID":"example", "src":"Hello <%= name %>, this is an example for email templating", "params":["name"]}
After successful execution of previous POST request we have created template with id example, now we 
can send it via email.

    curl -H "Content-Type: application/json" -X POST http://localhost:3000/api/v1/messageAsync 
    -d {"tempID":"example", "subject":"Subject example", "to":"example@email.com", "name":"junk-sender"}

With this API call we will send email to example@email.com with given subject and expanded message.
This call is async, and will return created message request that can be fetched from db.

## Future improvements

Add registered users and predefined groups for messages.
Externalise all configuration

## License

Copyright © 2019

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
