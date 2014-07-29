# gnar

**Goals**

* Take the functionality of a site like Lobsters or HN and strip it down to its essence.
* Add functionality to fit how [Bendyworks](http://bendyworks.com) uses [awesome](http://awesome.bendyworks.com)
* Make it super mobile friendly
* (someday) send daily digest emails automatically instead of having comment threads. (See: [awesome_digest](https://github.com/bendyworks/awesome_digest))

## Technical stuff

* Hoplon and Castra on Ring
* [HoneySQL](https://github.com/jkk/honeysql) for Postgres database
* Boot for tasks
* [friend](https://github.com/cemerick/friend/) is used for its bcrypt wrapper, but is not used as the authentication system.

User auth inspired by [tailrecursion/hoplon-demos](https://github.com/tailrecursion/hoplon-demos/blob/d9f2b726c5b89f4cdaf69fdaac007c69ea545599/castra-chat/src/castra/demo/http/rules.clj)

## Setup

**Loading initial DB structure**

1. Ensure postgresql is running.
1. Run `createdb gnar_development`
1. Run `psql gnar_development` to enter the Postgres shell
1. Load the DB structure with: `\i structure.sql`
1. It should be all set now.

**Starting the app**

1. Install [boot](https://github.com/tailrecursion/boot#getting-started)
   if you haven't already
1. Run `boot gnar-app`
1. Everything should be running! (`open http://localhost:8000`)

## License

Copyright © 2014 Matt Gauger

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
