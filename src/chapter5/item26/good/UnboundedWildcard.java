package chapter5.item26.good;

import java.util.List;

public class UnboundedWildcard {
//    @GetMapping("/item/{id}")
//    public ResponseEntity<?> getItem(@PathVariable Long id) {
//        try {
//            Item item = itemService.findById(id);
//            return ResponseEntity.ok(item); // 성공 시, item 반환
//        } catch (ItemNotFoundException e) {
//            return ResponseEntity.notFound().build(); // 실패 시, 404 Not Found
//        }
//    }

    public void printList(List<?> list) {
        for (Object obj : list) {
            System.out.println(obj);
        }
    }

}
