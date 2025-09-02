package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    private NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Test
    void 상품주문() {
        //given
        Member member = createMember();

        Book book = createItem("JPA", 13000, 12);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야한다");
        assertEquals(13000 * orderCount, getOrder.getTotalPrice(), "주문가격은 가격 * 수량이다");
        assertEquals(10, book.getStockQuantity(), "주문수량만큼 재고가 줄어야한다");

    }

    @Test
    void 상품주문_재고수량초과() {
        //given
        Member member = createMember();
        Item item = createItem("JPA", 13000, 12);

        int orderCount = 13;

        //when, then
        assertThrows(
                NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(), orderCount)
        );
    }

    @Test
    void 주문취소() {
        //given
        Member member = createMember();
        Item item = createItem("JPA", 13000, 12);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order gerOrder = orderRepository.findOne(orderId);
        assertEquals(OrderStatus.CANCEL, gerOrder.getStatus(), "주문 취소시 상태는 CANCEL 상태");
        assertEquals(12, item.getStockQuantity(), "주문 취소시 원래 상품 재고로 원복");
    }

    private Book createItem(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("광주", "길거리", "12345"));
        em.persist(member);
        return member;
    }
}