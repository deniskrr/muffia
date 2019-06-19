import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'

admin.initializeApp()
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

function shuffle < T>(array: T[]): T[] {
if (!Array.isArray(array)) {
        throw new TypeError(`Expected an Array, got ${typeof array} instead.`);
    }

    const oldArray = [...array];
    let newArray = new Array<T>();

    while (oldArray.length) {
        const i = Math.floor(Math.random() * oldArray.length);
        newArray = newArray.concat(oldArray.splice(i, 1));
    }
    return newArray;
}

// Start game when
export const startGame = functions.https.onCall(async (data, context) => {
    console.log(data)
    let updatedGame = {}

    await admin.firestore().collection("games").doc(data)
        .get().then(gameSnapshot => {
            return gameSnapshot.data() // Get game object
        }).then(game => {
            if (game != undefined && Object.keys(game.players).length == 7) {
                game.period = 'NIGHT_ONE'

                let users = Object.keys(game.players)

                const roles = ['MAFIA', 'MAFIA', 'COP', 'DOCTOR', 'CITIZEN', 'CITIZEN', 'CITIZEN']

                users = shuffle(users)

                // Map every user to a role
                for (let i = 0; i < users.length; i++) {
                    game.players[users[i]] = roles[i]
                }

                updatedGame = game
            }
        })

    // Update the game in the DB
    return admin.firestore().collection("games").doc(data).update(updatedGame).then().catch()
})
      
    

