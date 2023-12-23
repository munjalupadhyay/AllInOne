brew services start cassandra
brew services stop cassandra

brew services start mongodb/brew/mongodb-community



MONGO commands :

db.createUser(
   {
     user: "root",
     pwd: "r@@t",
     roles: [
       { role: "userAdminAnyDatabase", db: "admin" },
       { role: "readWriteAnyDatabase", db: "admin" }
     ]
   }
 )

db.createUser({user: "carrom", pwd:  "carrom",roles: [{ role: "readWrite", db: "myDB" }]})