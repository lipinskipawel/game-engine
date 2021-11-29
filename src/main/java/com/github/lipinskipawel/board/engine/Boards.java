package com.github.lipinskipawel.board.engine;

/**
 * This class is a facade for the {@link Board} API. It is expected to create {@link Board} implementations using this
 * facade.
 */
public class Boards {

    /**
     * This method will create an immutable {@link Board} object.
     *
     * @return board object
     */
    public static Board<Player> immutableBoard() {
        return new ImmutableBoard<>(new PlayerProvider<>(Player.FIRST, Player.SECOND));
    }

    /**
     * This method will create an immutable {@link Board} object parametrized by player object.
     *
     * <p>This method allows passing custom players to {@link Board} which will be then used in methods like
     * {@link Board#getPlayer()}, {@link Board#takeTheWinner()} and others.
     *
     * @param <T> type of the players
     * @return board object
     */
    public static <T> Board<T> immutableBoardWithCustomPlayer(final T first, final T second) {
        return new ImmutableBoard<>(new PlayerProvider<>(first, second));
    }
}
