import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'

admin.initializeApp()

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
            if (game != undefined && Object.keys(game.alivePlayers).length == 7) {
                game.period = 1

                let users = Object.keys(game.alivePlayers)

                const roles = ['MAFIA', 'MAFIA', 'COP', 'DOCTOR', 'CITIZEN', 'CITIZEN', 'CITIZEN']

                users = shuffle(users)

                // Map every user to a role
                for (let i = 0; i < users.length; i++) {
                    game.alivePlayers[users[i]] = roles[i]
                }

                updatedGame = game
            }
        })

    // Update the game in the DB
    return admin.firestore().collection("games").doc(data).update(updatedGame).then().catch()
})


export const newPeriod = functions.https.onCall(async (data, context) => {
        let updatedGame = {}

        await admin.firestore().collection("games").doc(data)
            .get().then(gameSnapshot => {
                return gameSnapshot.data() // Get game object
            }).then(game => {
                if (game != undefined && Object.keys(game.votes[game.period - 1]).length == 4) {
                    let mafiaVotes: string[] = []
                    let copVotes: string[] = []
                    let doctorVotes: string[] = []

                    Object.values<string>(game.votes[game.period - 1]).forEach((voteString: String) => {
                        let voteArray = voteString.split(",")
                        let votedUser = voteArray[0]
                        let votedRole = voteArray[1]
                        switch (votedRole) {
                            case 'MAFIA': {
                                mafiaVotes.push(votedUser)
                                break;
                            }
                            case 'COP': {
                                copVotes.push(votedUser)
                                break;
                            }
                            case 'DOCTOR': {
                                doctorVotes.push(votedUser);
                                break
                            }
                        }
                    })

                    //TODO: sort votes by frequency and select the most frequent for each roleVote

                    let killed = mafiaVotes[0]
                    let saved = doctorVotes[0]
                    let investigated = copVotes[0]


                    if (game.alivePlayers[investigated] == 'MAFIA') {
                        game.investigatedPlayers[investigated] = 'MAFIA'
                    } else {
                        game.investigatedPlayers[investigated] = 'CITIZEN'
                    }

                    if (killed !== saved) {
                        delete game.alivePlayers[killed]
                        game.deadPlayers.push(killed)
                    }

                    game.period += 1

                    updatedGame = game

                }
            })
        // Update the game in the DB
        return admin.firestore().collection("games").doc(data).update(updatedGame).then().catch()
    }
)
