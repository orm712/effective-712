package chapter4.item20;

public interface Athlete2 {
    void 근력운동();
    void 체력증진();
    void 연습();
    void 루틴();
}

abstract class BallSportsAthlete implements Athlete2{
    @Override
    public void 근력운동() {
        System.out.println("웨이트");
    }

    @Override
    public void 체력증진() {
        System.out.println("러닝");
    }

    @Override
    public void 루틴() {
        근력운동();
        체력증진();
        연습();
    }
}

class SoccerPlayer2 extends BallSportsAthlete implements Athlete2{
    @Override
    public void 연습() {
        System.out.println("드리블 연습");
    }
}

class BasketBallPlayer2 extends BallSportsAthlete implements Athlete2{
    @Override
    public void 연습() {
        System.out.println("자유투 연습");
    }
}