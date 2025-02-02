package chapter4.item20;

public interface Athlete {
    void 근력운동();
    void 체력증진();
    void 연습();
    void 루틴();
}

class SoccerPlayer implements Athlete{

    @Override
    public void 근력운동() {
        System.out.println("웨이트");
    }

    @Override
    public void 체력증진() {
        System.out.println("등산");
    }

    @Override
    public void 연습() {
        System.out.println("드리블 연습");
    }

    @Override
    public void 루틴() {
        근력운동();
        체력증진();
        연습();
    }
}

class BasketBallPlayer implements Athlete{

    @Override
    public void 근력운동() {
        System.out.println("웨이트");
    }

    @Override
    public void 체력증진() {
        System.out.println("등산");
    }

    @Override
    public void 연습() {
        System.out.println("자유투 연습");
    }

    @Override
    public void 루틴() {
        근력운동();
        체력증진();
        연습();
    }
}