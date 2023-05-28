package top.rrricardo.postcalendarbackend.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class CustomListTest {

    @Test
    void CustomListAdd1() {
        var list = new CustomList<Integer>();

        for(var i = 0; i < 100 ; i++) {
            list.add(i);
        }

        for (var i = 0 ; i < 100; i++) {
            Assertions.assertEquals(list.get(i), i);
            Assertions.assertEquals(list.indexOf(i), i);
            Assertions.assertTrue(list.contains(i));
        }

        Assertions.assertEquals(list.getSize(), 100);
        Assertions.assertEquals(-1, list.indexOf(null));
    }

    @Test
    void CustomListAdd2() {
        var expect = new ArrayList<Integer>();
        var random = new Random();

        for (var i = 0; i < 100; i++) {
            expect.add(random.nextInt());
        }

        var list = new CustomList<>(expect);

        for (var i = 50; i < 100; i++) {
             Assertions.assertEquals(list.remove(50), expect.get(i));
        }

        for (var i = 0; i < 50; i++) {
            Assertions.assertEquals(list.get(i), expect.get(i));
        }
    }

    @Test
    void CustomListSet() {
        var expect = new ArrayList<Integer>();
        var list = new CustomList<Integer>(100);
        var random = new Random();

        for (var i = 0; i < 100000; i++) {
            list.add(i);
            expect.add(i);
        }

        for (var i = 0; i < 10000; i++) {
            var index = random.nextInt(100000);
            var input = random.nextInt();

            list.set(index, input);
            expect.set(index, input);
        }

        for (var i = 0; i < 100000; i++) {
            Assertions.assertEquals(expect.get(i), list.get(i));
        }
    }

    @Test
    void CustomListIterator() {
        var expect = new ArrayList<Integer>();
        var list = new CustomList<Integer>();
        var random = new Random();

        for (var i = 0; i < 100; i++) {
            var input = random.nextInt();

            list.add(input);
            expect.add(input);
        }

        var i = 0;
        for(var item : list) {
            Assertions.assertEquals(expect.get(i), item);
            i++;
        }
    }

    @Test
    void CustomListTransfer() {
        var expect = new ArrayList<Integer>();
        var random = new Random();

        for (var i = 0; i < 100; i++) {
            expect.add(random.nextInt());
        }

        var list = new CustomList<>(expect);
        var actual = list.toList();

        for (var i = 0; i < 100; i++) {
            Assertions.assertEquals(actual.get(i), expect.get(i));
        }
    }

    @Test
    void testSort1() {
        var expect = new ArrayList<Integer>();
        var actual = new CustomList<Integer>();
        Random random = new Random();

        for(int i = 0; i < 100; i++){
            var input = random.nextInt();
            expect.add(input);
            actual.add(input);
        }

        actual.sort(Comparator.naturalOrder());
        expect.sort(Comparator.naturalOrder());

        for (var i = 0; i < 100; i++) {
            Assertions.assertEquals(expect.get(i), actual.get(i));
        }
    }
}
