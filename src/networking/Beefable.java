package networking;

import logic.Player;

public interface Beefable {
    //TODO opnå enighed om væsentligt ansvar og returtyper for Server

    /** Boots up the server, making it ready
     * to accept requests to join an ongoing game
     * @return void */
    public abstract void boot(int port);

    /** Instantiates a Player within a Thread, who is waiting
     * to join an ongoing game. The players avatar is placed on the
     * board after short delay on next available server tick
     * @throws PlayerJoinExeption */
    public abstract void initializePlayer(Player newPlayer);

    /** Polls current players for their chosen input, queues each player input,
     * resolves the outcome of each input (prioritized by the logical time of input occurence),
     * and increments server time by ? (1? the amount of inputs?) */
    public abstract void tick();

    /** Resolves the outcome of a tick/turn. Player inputs are queued
     * and prioritized in favor of time of input occurence
     * @returns true, if resolution is finished */
    public abstract boolean resolveOutcome();
}
