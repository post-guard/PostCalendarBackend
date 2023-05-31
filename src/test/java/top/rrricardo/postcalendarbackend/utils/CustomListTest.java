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

    @Test
    void testClear1() {
        var actual = new CustomList<Integer>();

        for (var i = 0; i < 100; i++) {
            actual.add(i);
        }

        Assertions.assertEquals(100, actual.getSize());

        actual.clear();
        Assertions.assertEquals(0, actual.getSize());

        for (var i = 0; i < 100; i++) {
            actual.add(i);
        }

        for (var i = 0; i < 100; i++) {
            Assertions.assertEquals(i, actual.get(i));
        }
    }

    @Test
    void testPolymerize() {
        var list1 = new CustomList<Integer>();
        list1.add(1);
        list1.add(4);
        list1.add(7);

        var list2 = new CustomList<Integer>();
        list2.add(2);
        list2.add(5);

        var list3 = new CustomList<Integer>();
        list3.add(3);
        list3.add(6);
        list3.add(8);

        var list4 = new CustomList<Integer>();

        var input = new CustomList<CustomList<Integer>>();
        input.add(list1);
        input.add(list2);
        input.add(list3);
        input.add(list4);

        var result = CustomList.polymerize(input);

        for (var i = 1; i < 9; i++) {
            Assertions.assertEquals(i, result.get(i - 1));
        }
    }
}
