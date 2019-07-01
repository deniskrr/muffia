import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
import _ = require('lodash');

admin.initializeApp()

export const startGame = functions.https.onCall(async (data, context) => {

    let updatedGame = {}

    await admin.firestore().collection("games").doc(data)
        .get().then(gameSnapshot => {
            return gameSnapshot.data() // Get game object
        }).then(game => {
            if (game !== undefined && _.keys(game.players).length === 7) {
                game.period = 1

                let users = _.keys(game.players)

                const roles: string[] = []

                _.times(game.mafiaCount, () => roles.push('MAFIA'))
                _.times(game.copCount, () => roles.push('COP'))
                _.times(game.doctorCount, () => roles.push('DOCTOR'))
                _.times(game.citizenCount, () => roles.push('CITIZEN'))

                users = _.shuffle(users)

                // Distribute the roles to players
                for (let i = 0; i < users.length; i++) {
                    game.players[users[i]] = roles[i]
                }

                game.alivePlayers = _.keys(game.players)

                updatedGame = game

            }
        })

    // Update the game in the DB
    return admin.firestore().collection("games").doc(data).update(updatedGame).then().catch()
})

function kill(game: any, killed: string) {
    switch (game.players[killed]) {
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

    // Remove the killed player from alive players list
    _.pull(game.alivePlayers, killed)

    // Add the killed player from dead players list
    game.deadPlayers.push(killed)

}


function getMostFrequentVote(votes: Array<String>): string {
    const voteCount = _.countBy(votes)
    const mostFrequent = _.maxBy(_.keys(voteCount), (votedPlayer: string) => voteCount[votedPlayer])
    if (mostFrequent !== undefined) {
        return mostFrequent
    }
    return ""
}

function investigate(game: any, investigated: string) {
    if (game.players[investigated] === 'MAFIA') {
        game.investigatedPlayers[investigated] = 'MAFIA'
    } else {
        game.investigatedPlayers[investigated] = 'CITIZEN'
    }
}

function getGameStatus(game: any): string {
    if (game.mafiaCount >= game.copCount + game.doctorCount + game.citizenCount) {
        return 'MAFIA'
    } else if (game.mafiaCount === 0) {
        return 'TOWN'
    }
    return 'PLAYING'
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
                if (game !== undefined) {

                    if (game.period % 2 === 1 && _.keys(game.votes[game.period - 1]).length ===
                        _.keys(game.alivePlayers).length - game.citizenCount) { // Night time and enough players voted

                        console.log("Night time")

                        const mafiaVotes: string[] = []
                        const copVotes: string[] = []
                        const doctorVotes: string[] = []

                        // Get the votes count for every party
                        _.values<string>(game.votes[game.period - 1]).forEach((voteString: String) => {
                            const voteArray = voteString.split(",")
                            const votedUser = voteArray[0]
                            const votedRole = voteArray[1]
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

                        // Perform actions based on votes
                        const killed = getMostFrequentVote(mafiaVotes)
                        let saved, investigated
                        if (doctorVotes.length > 0)
                            saved = getMostFrequentVote(doctorVotes)

                        if (copVotes.length > 0) {
                            investigated = getMostFrequentVote(copVotes)
                            investigate(game, investigated)
                        }
                        if (killed !== saved) {
                            kill(game, killed)
                        }

                        advancePeriod(game)

                    } else if (game.period % 2 === 0 &&
                        _.keys(game.votes[game.period - 1]).length === _.keys(game.alivePlayers).length) { // Day time and enough players voted

                        console.log("Day time")
                        const lynchVotes = _.values(game.votes[game.period - 1])
                        const lynchedPlayer = getMostFrequentVote(lynchVotes)
                        // Kill it
                        if (lynchedPlayer !== undefined) kill(game, lynchedPlayer)

                        advancePeriod(game)
                    }

                    const status = getGameStatus(game)
                    game.status = status
                    updatedGame = game
                }


            })
        // Update the game in the DB
        return admin.firestore().collection("games").doc(data).update(updatedGame).then().catch()
    }
)
