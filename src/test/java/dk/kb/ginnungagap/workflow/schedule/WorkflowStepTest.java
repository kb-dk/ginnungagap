package dk.kb.ginnungagap.workflow.schedule;

import java.util.Random;
import java.util.UUID;

import org.jaccept.structure.ExtendedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WorkflowStepTest extends ExtendedTestCase {

    @Test
    public void testStatus() {
        WorkflowStep step = new WorkflowStep(null) {
            @Override
            protected void performStep() throws Exception {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
            @Override
            public String getName() {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
        };
        
        Assert.assertEquals(step.getStatus(), WorkflowStep.INIT_STATUS);
        
        String newStatus = UUID.randomUUID().toString();
        step.setStatus(newStatus);
        Assert.assertEquals(step.getStatus(), newStatus);
    }

    @Test
    public void testResults() {
        WorkflowStep step = new WorkflowStep(null) {
            @Override
            protected void performStep() throws Exception {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
            @Override
            public String getName() {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
        };
        
        Assert.assertEquals(step.getResultOfLastRun(), WorkflowStep.INIT_RESULTS);
        
        String newResults = UUID.randomUUID().toString();
        step.setResultOfRun(newResults);
        Assert.assertEquals(step.getResultOfLastRun(), newResults);
    }

    @Test
    public void testLastRunWhenNotRunning() {
        WorkflowStep step = new WorkflowStep(null) {
            @Override
            protected void performStep() throws Exception {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
            @Override
            public String getName() {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
        };
        
        Assert.assertEquals(Long.parseLong(step.getExecutionTime()), WorkflowStep.INIT_LAST_RUN_TIME.longValue());
        Assert.assertFalse(step.getExecutionTime().contains(WorkflowStep.RUNNING_TIME_SUFFIX));
        
        Long l = Math.abs(new Random().nextLong());
        step.timeForLastRun = l;

        Assert.assertEquals(Long.parseLong(step.getExecutionTime()), l.longValue());
        Assert.assertFalse(step.getExecutionTime().contains(WorkflowStep.RUNNING_TIME_SUFFIX));
    }

    @Test
    public void testLastRunWhenRunning() {
        WorkflowStep step = new WorkflowStep(null) {
            @Override
            protected void performStep() throws Exception {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
            @Override
            public String getName() {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
        };
        
        addStep("Set the start of step", "");
        step.currentRunStart = 1L;
        
        Assert.assertTrue(step.getExecutionTime().contains(WorkflowStep.RUNNING_TIME_SUFFIX));
    }

    @Test
    public void testRunSuccess() {
        WorkflowStep step = new WorkflowStep(null) {
            @Override
            protected void performStep() throws Exception {
                // SHOULD ENTER HERE
            }
            @Override
            public String getName() {
                throw new IllegalStateException("SHOULD NOT TEST THIS!");
            }
        };
        
        step.run();
        
        Assert.assertEquals(step.getStatus(), WorkflowStep.STATUS_FINISHED);
    }

    @Test
    public void testRunFailed() {
        WorkflowStep step = new WorkflowStep(null) {
            @Override
            protected void performStep() throws Exception {
                throw new IllegalStateException("SHOULD THROW AN EXCEPTION");
            }
            @Override
            public String getName() {
                return "TEST";
            }
        };
        
        step.run();
        
        Assert.assertEquals(step.getStatus(), WorkflowStep.STATUS_FAILED);
    }

}
