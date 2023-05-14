package top.rrricardo.postcalendarbackend.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;

import java.util.Hashtable;
import java.util.Random;

public class CustomHashTableTest {

    @Test
    void testHashTablePut1() {
        var table = new CustomHashTable<Integer, Integer>();
        var except = new Hashtable<Integer, Integer>();
        var random = new Random();

        for (var i = 0; i < 50; i++) {
            var key = random.nextInt();
            var value = random.nextInt();

            table.put(key, value);
            except.put(key, value);
        }

        System.out.println("Except: ");
        for (var pair : except.entrySet()) {
            System.out.println(pair.getKey() + "-" + pair.getValue());
        }

        System.out.println("Actual: ");
        for (var pair : table) {
            System.out.println(pair.getKey() + "-" + pair.getValue());
        }

        Assertions.assertEquals(except.size(), table.getSize());
        for (var key : except.keySet()) {
            Assertions.assertEquals(except.get(key), table.get(key));
        }
    }
}
