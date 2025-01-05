package chapter1.item7;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// scheduledThreadPoolExecutor를 사용하는 간단한 예제
public class STPE {
    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(3);
    static LinkedHashMap<String, String> map = new LinkedHashMap<>();
    public static void main(String[] args) throws Exception{
        // Runnable의 run 메서드를 실행하는 작업을 최초 5초(initialDelay)후에 실행한 뒤 5초(period)마다 반복적으로 실행
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
                System.out.println(" ============== ");
                while(iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    System.out.println(entry);
                    // 특정 조건에 해당하는 엔트리를 제거
                    if(entry.getValue().equals("TARGET")) {
                        iterator.remove();
                    }
                }
                System.out.println(" ============== ");
            }
        }, 5L, 5L, TimeUnit.SECONDS);
        map.put("KIM", "TARGET");
        map.put("KI", "TRGET");
        map.put("K", "TRGET");
    }
}
