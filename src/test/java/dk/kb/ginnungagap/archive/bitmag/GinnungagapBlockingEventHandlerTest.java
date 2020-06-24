package dk.kb.ginnungagap.archive.bitmag;

import org.bitrepository.bitrepositoryelements.ResponseCode;
import org.bitrepository.client.eventhandler.ContributorCompleteEvent;
import org.bitrepository.client.eventhandler.ContributorFailedEvent;
import org.bitrepository.common.settings.Settings;
import org.bitrepository.common.settings.SettingsProvider;
import org.bitrepository.common.settings.XMLFileSettingsLoader;
import org.bitrepository.common.utils.SettingsUtils;
import org.bitrepository.settings.repositorysettings.Collection;
import org.bitrepository.settings.repositorysettings.Collections;
import org.bitrepository.settings.repositorysettings.PillarIDs;
import org.bitrepository.settings.repositorysettings.RepositorySettings;
import org.jaccept.structure.ExtendedTestCase;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

/**
 * Tests for {@link dk.kb.ginnungagap.archive.bitmag.Bitrepository }
 * Named BitrepositoryTester and not BitrepositoryTest to avoid inclusion in
 * the set of unittests run by Maven.
 */
public class GinnungagapBlockingEventHandlerTest extends ExtendedTestCase {

    private static String COLLECTION_ID_1;
    private static String PILLAR_1;
    private static String PILLAR_2;

    @BeforeClass
    public static void beforeClass() throws Exception {
        COLLECTION_ID_1 = "THE COLLECTION - " + UUID.randomUUID().toString();
        PILLAR_1 = "pillar-" + UUID.randomUUID().toString();
        PILLAR_2 = "pillar-" + UUID.randomUUID().toString();

        Settings settings = Mockito.mock(Settings.class);
        Collection collection = Mockito.mock(Collection.class);
        Mockito.when(collection.getID()).thenReturn(COLLECTION_ID_1);
        PillarIDs pillars = Mockito.mock(PillarIDs.class);
        Mockito.when(pillars.getPillarID()).thenReturn(Arrays.asList(PILLAR_1, PILLAR_2));
        Mockito.when(collection.getPillarIDs()).thenReturn(pillars);
        Mockito.when(settings.getCollections()).thenReturn(Arrays.asList(collection));


        Collections collections = Mockito.mock(Collections.class);
        Mockito.when(collections.getCollection()).thenReturn(Arrays.asList(collection));
        RepositorySettings repSettings = Mockito.mock(RepositorySettings.class);
        Mockito.when(repSettings.getCollections()).thenReturn(collections);
        Mockito.when(settings.getRepositorySettings()).thenReturn(repSettings);
        SettingsUtils.initialize(settings);

        SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1);
    }

    @Test
    public void testBeforeEvents() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 0);
        assertEquals(0, gbeh.getResults().size());
        assertEquals(0, gbeh.getFailures().size());
        assertFalse(gbeh.hasFailed(),"The operation should be considered a failure, before any events");
    }

    @Test
    public void testOnlySuccess() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 0);
        for(String pillarId : SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1)) {
            gbeh.handleEvent(new ContributorCompleteEvent(pillarId, COLLECTION_ID_1));
        }
        assertEquals(SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1).size(), gbeh.getResults().size());
        assertEquals(0, gbeh.getFailures().size());
        assertFalse(gbeh.hasFailed(), "The operation should not be considered a failure, when every pillar has succeeded");
    }

    @Test
    public void testOnlyFailures() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 0);
        for(String pillarId : SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1)) {
            gbeh.handleEvent(new ContributorFailedEvent(pillarId, COLLECTION_ID_1, ResponseCode.FAILURE));
        }
        assertEquals(0, gbeh.getResults().size());
        assertEquals(SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1).size(), gbeh.getFailures().size());
        assertTrue(gbeh.hasFailed(), "The operation should be considered a failure, when every pillar has failed");
    }

    @Test
    public void testOneFailureWhenNoneAllowed() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 0);
        for(String pillarId : SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1)) {
            if(pillarId.equals(PILLAR_1)) {
                gbeh.handleEvent(new ContributorFailedEvent(pillarId, COLLECTION_ID_1, ResponseCode.FAILURE));
            } else {
                gbeh.handleEvent(new ContributorCompleteEvent(pillarId, COLLECTION_ID_1));
            }
        }
        assertEquals(SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1).size()-1, gbeh.getResults().size());
        assertEquals(1, gbeh.getFailures().size());
        assertTrue(gbeh.hasFailed(), "The operation should be considered a failure");
    }

    @Test
    public void testOneFailureWhenOneAllowed() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 1);
        for(String pillarId : SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1)) {
            if(pillarId.equals(PILLAR_1)) {
                gbeh.handleEvent(new ContributorFailedEvent(pillarId, COLLECTION_ID_1, ResponseCode.FAILURE));
            } else {
                gbeh.handleEvent(new ContributorCompleteEvent(pillarId, COLLECTION_ID_1));
            }
        }
        assertEquals(SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1).size()-1, gbeh.getResults().size());
        assertEquals(1, gbeh.getFailures().size());
        assertFalse(gbeh.hasFailed(), "The operation should not be considered a failure");
    }
    
    @Test
    public void testOneFailureWhenTwoAllowed() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 2);
        for(String pillarId : SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1)) {
            if(pillarId.equals(PILLAR_1)) {
                gbeh.handleEvent(new ContributorFailedEvent(pillarId, COLLECTION_ID_1, ResponseCode.FAILURE));
            } else {
                gbeh.handleEvent(new ContributorCompleteEvent(pillarId, COLLECTION_ID_1));
            }
        }
        assertEquals(SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1).size()-1, gbeh.getResults().size());
        assertEquals(1, gbeh.getFailures().size());
        assertFalse(gbeh.hasFailed(), "The operation should not be considered a failure");
    }
    
    @Test
    public void testTwoFailuresWhenOneAllowed() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 0);
        for(String pillarId : SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1)) {
            if(pillarId.equals(PILLAR_1) || pillarId.equals(PILLAR_2)) {
                gbeh.handleEvent(new ContributorFailedEvent(pillarId, COLLECTION_ID_1, ResponseCode.FAILURE));
            } else {
                gbeh.handleEvent(new ContributorCompleteEvent(pillarId, COLLECTION_ID_1));
            }
        }
        assertEquals(SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1).size()-2, gbeh.getResults().size());
        assertEquals(2, gbeh.getFailures().size());
        assertTrue(gbeh.hasFailed(), "The operation should be considered a failure");
    }
    
    @Test
    public void testOnlyOneFailure() {
        GinnungagapBlockingEventHandler gbeh = new GinnungagapBlockingEventHandler(COLLECTION_ID_1, 0);
        gbeh.handleEvent(new ContributorFailedEvent(SettingsUtils.getPillarIDsForCollection(COLLECTION_ID_1).get(0), COLLECTION_ID_1, ResponseCode.FAILURE));
        assertEquals(0, gbeh.getResults().size());
        assertTrue(gbeh.hasFailed(), "The operation should be considered a failure, when every pillar has failed");
    }
}
