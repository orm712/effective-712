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

//속 계층에서 인터페이스 구현이 자동으로 전달되기 때문에, 이미 상위 클래스인 BallSportsAthlete가 Athlete2를 구현하고 있다면, 하위 클래스인 SoccerPlayer2에서 다시 명시적으로 implements Athlete2를 선언할 필요가 없습니다.
class SoccerPlayer2 extends BallSportsAthlete{
    @Override
    public void 연습() {
        System.out.println("드리블 연습");
    }
}

class BasketBallPlayer2 extends BallSportsAthlete{
    @Override
    public void 연습() {
        System.out.println("자유투 연습");
    }
}