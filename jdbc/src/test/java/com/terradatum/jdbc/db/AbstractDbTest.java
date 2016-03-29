package com.terradatum.jdbc.db;

import com.ninja_squad.dbsetup.operation.Operation;
import com.terradatum.jdbc.AbstractAdapterTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;

/**
 * @author rbellamy
 * @date 3/20/16
 */
public class AbstractDbTest extends AbstractAdapterTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDbTest.class);
  private static boolean firstRun = true;

  protected Operation setupDb() throws Exception {

    Operation operation = sequenceOf(
        DbOperations.DELETE_ALL,
        DbOperations.INSERT_ALL
    );
    if (firstRun) {
      LOGGER.info(operation.toString());
      firstRun = false;
    }
    return operation;
  }
}
