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
export const startGame = functions.firestore.document("games/{roomID}")
.onUpdate(change => {
        let game = change.after.data()
        // If there are 7 players in the lobby, start the game
        if (game !== undefined) {
            if (Object.keys(game.players).length ===     7 && game.period === "NOT_STARTED") {            
            game.period = "NIGHT_ONE" // Change the period to night 1
            let users = Object.keys(game.players)
            
            users = shuffle(users)

            const mafia = 2, cop = 1, doc = 1, citizens = 3
            
            for (let i = 0; i< mafia; i++) {
                game.players[users[i]] = 'MAFIA'
            }

            for (let i = mafia; i< mafia + cop; i++) {
                game.players[users[i]] = 'COP'
            }

            for (let i = mafia+cop; i< mafia+cop+doc; i++) {
                game.players[users[i]] = 'DOCTOR'
            }

            for (let i = mafia+cop+doc; i< mafia+cop+doc+citizens; i++) {
                game.players[users[i]] = 'CITIZEN'
            }

            change.before.ref.update(game).then().catch()
            }
        }
        return null;
    })

