package is.rebbi.wo.formatters;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith( Suite.class )
@Suite.SuiteClasses( { TestDurationFormatter.class, TestDurationFormatterDecimal.class, TestPercentageFormatter.class, TestStandardDurationFormatter.class } )
public class TestSuite {}