import java.util.*;

public class GameState implements Cloneable {
  List<TestTube> tubes;

  static class TestTube implements Cloneable {
    public static final int MAX_HEIGHT = 4;

    Stack<Integer> colors;

    public TestTube() {
      colors = new Stack<>();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TestTube testTube = (TestTube) o;
      return colors.equals(testTube.colors);
    }

    @Override
    public int hashCode() {
      return Objects.hash(colors);
    }

    boolean pourInto(TestTube dest) {
      // this == source
      if (dest.colors.size() == TestTube.MAX_HEIGHT)
        return false;

      if (this.colors.isEmpty())
        return false;

      if (!dest.colors.isEmpty() && !dest.colors.peek().equals(this.colors.peek())) {
        return false;
      }

      int color = this.colors.peek();
      while (!this.colors.isEmpty() && this.colors.peek() == color && dest.colors.size() != TestTube.MAX_HEIGHT) {
        dest.colors.push(this.colors.pop());
      }

      return true;
    }

    boolean isMonochromatic() {
      if (this.colors.isEmpty())
        return true;

      if (this.colors.size() != TestTube.MAX_HEIGHT)
        return false;

      int topColor = this.colors.peek();
      for (int color : this.colors) { // enhanced for-loop
        if (color != topColor)
          return false;
      }

      return true;
    }

    @Override
    public TestTube clone() {
      try {
        TestTube clone = (TestTube) super.clone();
        clone.colors = (Stack<Integer>) this.colors.clone();
        return clone;
      } catch (CloneNotSupportedException e) {
        throw new AssertionError();
      }
    }
  }

  public GameState() {
    tubes = new ArrayList<>();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GameState gameState = (GameState) o;
    return tubes.equals(gameState.tubes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tubes);
  }

  @Override
  public GameState clone() {
    try {
      GameState clone = (GameState) super.clone();
      clone.tubes = new ArrayList<>();
      for (TestTube tube : this.tubes)
        clone.tubes.add(tube.clone());
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  boolean isFinished() {
    for (TestTube tube : tubes) {
      if (!tube.isMonochromatic())
        return false;
    }

    return true;
  }


  public static class Move {
    int fromIdx;
    int toIdx;

    @Override
    public String toString() {
      return "Move{" +
              "fromIdx=" + fromIdx +
              ", toIdx=" + toIdx +
              "}\n";
    }
  }

  public static boolean trace(GameState currentState, Stack<Move> moves, Set<GameState> pastStates) {
    if (currentState.isFinished())
      return true;

    for (int firstIdx = 0; firstIdx < currentState.tubes.size(); firstIdx++) {
      for (int secondIdx = 0; secondIdx < currentState.tubes.size(); secondIdx++) {
        if (secondIdx == firstIdx)
          continue;
        GameState nextState = currentState.clone();


        TestTube first = nextState.tubes.get(firstIdx);
        TestTube second = nextState.tubes.get(secondIdx);

        if (first.isMonochromatic() && second.isMonochromatic())
          continue; // Dumb useless move, don't try it

        if (!first.pourInto(second))
          continue;

        if (pastStates.contains(nextState))
          continue;

        pastStates.add(nextState);

        Move next = new Move();
        next.fromIdx = firstIdx;
        next.toIdx = secondIdx;
        moves.push(next);

        if (trace(nextState, moves, pastStates))
          return true;

        moves.pop();
      }
    }

    return false;
  }

  public static final int
          YELLOW = 0,
          ORANGE = 1,
          VIOLET = 2,
          VINE = 3,
          SALMON = 4,
          GREY = 5,
          LIME = 6,
          RED = 7,
          GREEN = 8,
          PURPLE = 9,
          BROWN = 10,
          BLUE = 11;

  public static TestTube flashMaker(Integer first, Integer second, Integer third, Integer fourth) {
    TestTube tube = new TestTube();
    if (first != null)
      tube.colors.push(first);
    if (second != null)
      tube.colors.push(second);
    if (third != null)
      tube.colors.push(third);
    if (fourth != null)
      tube.colors.push(fourth);
    return tube;
  }

  public static void main(String... args) {
    GameState initialState = new GameState();

    // Populate the list of tubes
    initialState.tubes.add(flashMaker(YELLOW, VIOLET, ORANGE, YELLOW));
    initialState.tubes.add(flashMaker(GREY, SALMON, YELLOW, VINE));
    initialState.tubes.add(flashMaker(LIME, VIOLET, SALMON, VIOLET));
    initialState.tubes.add(flashMaker(GREEN, YELLOW, RED, GREY));
    initialState.tubes.add(flashMaker(RED, PURPLE, GREY, VINE));
    initialState.tubes.add(flashMaker(LIME, BROWN, VIOLET, ORANGE));
    initialState.tubes.add(flashMaker(PURPLE, RED, BLUE, GREEN));
    initialState.tubes.add(flashMaker(BROWN, SALMON, BLUE, LIME));
    initialState.tubes.add(flashMaker(BROWN, GREEN, SALMON, VINE));
    initialState.tubes.add(flashMaker(GREEN, BLUE, PURPLE, BROWN));
    initialState.tubes.add(flashMaker(VINE, LIME, ORANGE, GREY));
    initialState.tubes.add(flashMaker(RED, PURPLE, ORANGE, BLUE));
    initialState.tubes.add(flashMaker(null, null, null, null));
    initialState.tubes.add(flashMaker(null, null, null, null));

    Stack<Move> moves = new Stack<>();
    Set<GameState> pastStates = new HashSet<>();
    boolean winnable = GameState.trace(initialState, moves, pastStates);

    if (!winnable) {
      System.out.println("OH NOOOOS! ;(");
      return;
    }

    System.out.println("Winnable with the following set of " + moves.size() + " moves:");
    System.out.println(moves);

  }
}


