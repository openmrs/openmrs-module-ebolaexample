package org.openmrs.module.ebolaexample.metadata;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

/**
 * Use this to create concepts that we know will be coming down the pipe from CIEL, but haven't been released yet.
 * Once a released CIEL dictionary includes these concepts, we can remove them from this class
 */
@Component("missingConceptsBundle")
public class MissingConceptsBundle extends AbstractMetadataBundle {

    @Override
    public void install() throws Exception {
        // Note that the UUID of the CIEL concept source was 249b13c8-72fa-4b96-8d3d-b200efed985e in 2015 (when this
        // module was being built) but it is now 21ADDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD (as we're setting up a new demo
        // server in 2018)
        
        // this intentionally does nothing, since CIEL now includes all concepts. We leave the file here to make it easier
        // for someone to see its git history and see how to install missing concepts, in case of another emergency response
    }

}
