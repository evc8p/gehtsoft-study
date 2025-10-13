import com.evch.rrm.Bank;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankTests {
    @Test
    void getSumOfAllAccountsMethodShouldReturnSameResultBeforeAndAfterTransferOfFunds1000Times() {
        Bank bank = new Bank(200, 0, 1000);
        for (int i = 0; i < 1000; i++) {
            BigInteger sumBefore = bank.getSumOfAllAccounts();
            bank.startTransfers();
            BigInteger sumAfter = bank.getSumOfAllAccounts();
            assertEquals(sumBefore, sumAfter,
                    "Sum before the transfer is not equals to sum after the transfer\n on iteration = " + i);
        }
    }
}
