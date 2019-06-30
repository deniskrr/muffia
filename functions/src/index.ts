import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
import _ = require('lodash');

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

                const roles = []

                for (let i = 0; i < game.copCount; i++) {
                    roles.push('COP')
                }

                for (let i = 0; i < game.doctorCount; i++) {
                    roles.push('DOCTOR')
                }

                for (let i = 0; i < game.mafiaCount; i++) {
                    roles.push('MAFIA')
                }

                for (let i = 0; i < game.citizenCount; i++) {
                    roles.push('CITIZEN')
                }

                users = shuffle(users)

                // Distribute the roles to players
                for (let i = 0; i < users.length; i++) {
                    game.alivePlayers[users[i]] = roles[i]
                }

                updatedGame = game
            }
        })

    // Update the game in the DB
    return admin.firestore().collection("games").doc(data).update(updatedGame).then().catch()
})

function kill(game: any, killed: string) {
    switch (game.alivePlayers[killed]) {
        case 'MAFIA': {
            game.mafiaCount--
            break;
        }
        case 'COP': {
            game.copCount--
            break;
        }
        case 'DOCTOR': {
            game.doctorCount--
            break
        }
        case 'CITIZEN': {
            game.citizenCount--;
            break;
        }
    }

    delete game.alivePlayers[killed]
    game.deadPlayers.push(killed)

}


function investigate(game: any, investigated: string) {
    if (game.alivePlayers[investigated] == 'MAFIA') {
        game.investigatedPlayers[investigated] = 'MAFIA'
    } else {
        game.investigatedPlayers[investigated] = 'CITIZEN'
    }
}

function advancePeriod(game: any) {
    game.period++
    game.votes.push({})
}

export const newPeriod = functions.https.onCall(async (data, context) => {
        let updatedGame = {}

        await admin.firestore().collection("games").doc(data)
            .get().then(gameSnapshot => {
                return gameSnapshot.data() // Get game object
            }).then(game => {
                if (game != undefined) {
                    if (game.period % 2 == 1 && Object.keys(game.votes[game.period - 1]).length ==
                        Object.keys(game.alivePlayers).length - game.citizenCount) { // Night time and enough players voted

                        console.log("Night time")

                        let mafiaVotes: string[] = []
                        let copVotes: string[] = []
                        let doctorVotes: string[] = []

                        // Get the votes count for every party
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


                        // Perform actions based on votes
                        investigate(game, investigated)

                        if (killed !== saved) {
                            kill(game, killed)
                        }

                        advancePeriod(game)

                        updatedGame = game

                    } else if (game.period % 2 == 0 &&
                        Object.keys(game.votes[game.period - 1]).length == Object.keys(game.alivePlayers).length) { // Day time and enough players voted

                        console.log("Day time")

                        // Compute the voting count for every voted player
                        let votes = Object.values<string>(game.votes[game.period - 1])
                            .reduce((a, c) => {
                                    a[c] = (a[c] || 0) + 1
                                    return a
                                }, Object.create(null)
)

// Get the most voted player
var lynchedPlayer = _.maxBy(_.keys(votes), function (o) {
return votes[o];
});

// Kill it
if (lynchedPlayer !== undefined) kill(game, lynchedPlayer)

                        advancePeriod(game)

                        updatedGame = game

                        console.log(votes)
                    }
                }
            })
// Update the game in the DB
        return admin.firestore().collection("games").doc(data).update(updatedGame).then().catch()
    }
)
