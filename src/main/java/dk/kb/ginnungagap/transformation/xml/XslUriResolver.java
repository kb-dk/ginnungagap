package dk.kb.ginnungagap.transformation.xml;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

/**
 * Implements an XSL URI resolver which can be used to resolve external XSL files.
 * Unused - so a project for those dark January nights.
 */
public class XslUriResolver implements URIResolver {

    /** Logging mechanism. */
    private static Logger logger = LoggerFactory.getLogger(XslUriResolver.class.getName());

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        logger.info("URIResolver: href=" + href + " - base=" + base);
        throw new NotImplementedException("XslUriResolver.resolve(String href, String base)");
    }

}
