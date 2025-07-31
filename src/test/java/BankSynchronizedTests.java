import com.evch.rrm.BankSynchronized;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankSynchronizedTests {
    @Test
    void getSumOfAllAccountsMethodShouldReturnSameResultBeforeAndAfterTransferOfFunds1000Times() {
        BankSynchronized bank = new BankSynchronized(200, 0, 1000);
        for (int i = 0; i < 1000; i++) {
            BigInteger sumBefore = bank.getSumOfAllAccounts();
            bank.startTransfers();
            BigInteger sumAfter = bank.getSumOfAllAccounts();
            assertEquals(sumBefore, sumAfter,
                    "Sum before the transfer is not equals to sum after the transfer on iteration = " + i);
        }
    }
}
