package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final initService initService;

    @PostConstruct
    void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class initService {
        private final EntityManager em;

        void dbInit1() {
            Member member = createMember("member1", "city1", "street1", "1234");
            em.persist(member);

            Book book = createBook("JPA1", 10000, 12);
            em.persist(book);

            Book book2 = createBook("JPA2", 12000, 64);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book, 7000, 10);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 8000, 20);

            Order order = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order);
        }

        void dbInit2() {
            Member member = createMember("member2", "city2", "street2", "4321");
            em.persist(member);

            Book book = createBook("Spring1", 20000, 120);
            em.persist(book);

            Book book2 = createBook("Spring2", 22000, 204);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book, 17000, 20);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 18000, 40);

            Order order = Order.createOrder(member, createDelivery(member), orderItem1, orderItem2);
            em.persist(order);
        }

        private Member createMember(String name, String city, String street,
                                    String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
