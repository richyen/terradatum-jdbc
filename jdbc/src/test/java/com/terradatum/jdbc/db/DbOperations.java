package com.terradatum.jdbc.db;

import com.ninja_squad.dbsetup.generator.ValueGenerators;
import com.ninja_squad.dbsetup.operation.Operation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.ninja_squad.dbsetup.Operations.*;

/**
 * @author rbellamy@terradatum.com
 * @date 3/18/16
 */
public class DbOperations {
  public static final Operation DELETE_ALL = deleteAllFrom("child", "sub_parent", "parent");
  public static final Operation INSERT_ALL = insertAll();
  private static final Logger LOGGER = LoggerFactory.getLogger(DbOperations.class);

  public static Operation insertAll() {
    return sequenceOf(
        insertInto("parent")
            .withGeneratedValue("parent_id", ValueGenerators.sequence().startingAt(1L))
            .withGeneratedValue("some_date", ValueGenerators.dateSequence())
            .columns("parent_name", "some_timestamp")
            .values("Caesar", getInstantFromLocalDateTimeNow())
            .values("Maurice", getInstantFromLocalDateTimeNow())
            .values("Rocket", getInstantFromLocalDateTimeNow())
            .values("Buck", getInstantFromLocalDateTimeNow())
            .values("Cornelia", getInstantFromLocalDateTimeNow())
            .values("Alpha", getInstantFromLocalDateTimeNow())
            .values("Koba", getInstantFromLocalDateTimeNow())
            .build(),
        insertInto("sub_parent")
            .withGeneratedValue("parent_id", ValueGenerators.sequence().startingAt(1L))
            .columns("parent_type")
            .repeatingValues("ape").times(7)
            .build(),
        insertInto("child")
            .withGeneratedValue("child_id", ValueGenerators.sequence().startingAt(1L))
            .columns("parent_id", "child_name")
            .values(1, "Mickey Mouse")
            .values(1, "Donald Duck")
            .values(1, "Minny Mouse")
            .values(2, "Bugs Bunny")
            .values(2, "Daffy Duck")
            .values(3, "Scooby-Doo")
            .values(5, "Tigger")
            .values(5, "Pooh")
            .values(6, "Jerry")
            .values(7, "Tom")
            .build(),
        insertInto("parent")
            .withGeneratedValue("parent_id", ValueGenerators.sequence().startingAt(8L))
            .withGeneratedValue("some_date", ValueGenerators.dateSequence())
            .columns("parent_name", "some_timestamp")
            .values("Dr. Will Rodman", getInstantFromLocalDateTimeNow())
            .values("Caroline Aranha", getInstantFromLocalDateTimeNow())
            .values("Robert Franklin", getInstantFromLocalDateTimeNow())
            .build(),
        insertInto("sub_parent")
            .withGeneratedValue("parent_id", ValueGenerators.sequence().startingAt(8L))
            .columns("parent_type")
            .repeatingValues("human").times(3)
            .build(),
        insertInto("child")
            .withGeneratedValue("child_id", ValueGenerators.sequence().startingAt(11L))
            .columns("parent_id", "child_name")
            .values(8, "Bambi")
            .values(8, "Daisy Duck")
            .values(9, "Thumper")
            .values(9, "Donkey Kong")
            .values(9, "Donkey")
            .values(10, "Piglet")
            .build()
        );
  }

  @NotNull
  private static Date getInstantFromLocalDateTimeNow() {
    return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
  }
}
