import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'

admin.initializeApp()
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


// Start game when
export const startGame = functions.firestore.document("games/{roomID}")
.onUpdate(change => {
        let after = change.after.data()

        // If there are 7 players in the lobby, start the game
        if (after !== undefined && Object.keys(after.players).length == 7) {            
            after.period = "NIGHT_ONE"
            change.before.ref.update(after).then().catch()
        }
        return null;
    })

