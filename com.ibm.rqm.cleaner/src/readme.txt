OSLC Cleaner Utility Version 1.0
================================
The Open Services for Lifecycle Collaboration (OSLC) Cleaner Utility is an open-source stand-alone Java application used to clean OSLC Quality Management (QM) V2 and/or OSLC Requirements Management (RM) V2 resources including target OSLC Requirements Management (RM) V2, OSLC Change Management (CM) V2, and/or OSLC Quality Management (QM) V2 resources.  Cleaning involves one or more of the following supported commands:

Command: readAllResources
Description: Reads the OSLC resource feed(s) from the server for the resource types specified in the (optional) -rt/-resourceTypes argument and outputs the feed page(s) as RDF/XML to the console or file(s) under an output directory.  If an output directory is specified, a directory named 'OSLC' is created (or deleted and recreated, if exists) under the output directory, a directory for each project area (same name as the project area) is created (or deleted and recreated, if exists) under the 'OSLC' directory, and a directory for each resource type (same name as the resource type) is created (or deleted and recreated, if exists) under each project area directory.  Supports the -od/-outputDirectory and -cc/-configurationContexts arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.
Cause: When reading OSLC resource feeds, they may be paged, possibly requiring multiple HTTP GET requests.
Note: This command supports project areas enabled for configuration management, which requires the -cc/-configurationContexts argument containing a global configuration (stream or baseline) or local configuration (stream or baseline) context URL.
Support: See the -lr/-listResources argument.

Command: removeBrokenForwardLinks
Description: Removes from QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument all properties in the (optional) -ppn/-propertyPrefixedNames argument containing broken forward links (including associated forward link labels) to target RM (oslc_rm), CM (oslc_cm), and/or QM (oslc_qm) resources that do not exist (archived, soft-deleted, or deleted).  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.
Cause: When target RM (oslc_rm), CM (oslc_cm), and/or QM (oslc_qm) resources are deleted (archived, soft-deleted, or deleted), the properties containing forward links are not removed from the QM (oslc_qm) and/or RM (oslc_rm) resources.
Note: This command supports project areas enabled for configuration management for only QM (oslc_qm) resources containing forward links to target RM (oslc_rm) resources (see Support table), which requires the -cc/-configurationContexts argument containing a global configuration (stream) context URL.
Support:
	
Resource                      | Property                               | Configuration Context
----------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | Yes
oslc_qm:TestPlan              | oslc_qm:relatedChangeRequest           | No
oslc_qm:TestPlan              | calm:testsDevelopmentPlan              | No
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | Yes
oslc_qm:TestCase              | oslc_qm:relatedChangeRequest           | No
oslc_qm:TestCase              | oslc_qm:testsChangeRequest             | No
oslc_qm:TestScript            | oslc_qm:relatedChangeRequest           | No
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | Yes
oslc_qm:TestExecutionRecord   | oslc_qm:blockedByChangeRequest         | No
oslc_qm:TestExecutionRecord   | oslc_qm:relatedChangeRequest           | No
oslc_qm:TestResult            | oslc_qm:affectedByChangeRequest        | No
oslc_qm:TestResult            | oslc_qm:relatedChangeRequest           | No
rqm_qm:TestScriptStepResult   | oslc_qm:affectedByChangeRequest        | No
rqm_qm:TestScriptStepResult   | oslc_qm:relatedChangeRequest           | No
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | No
oslc_rm:Requirement           | oslc_rm:validatedBy                    | No

Command: removeAllForwardLinks
Description: Removes from QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument all properties in the (optional) -ppn/-propertyPrefixedNames argument containing forward links (including associated forward link labels) to target RM (oslc_rm), CM (oslc_cm), and/or QM (oslc_qm) resources.  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.
Cause: When properties containing forward links are not required.
Note: This command supports project areas enabled for configuration management for only QM (oslc_qm) resources containing forward links to target RM (oslc_rm) resources (see Support table), which requires the -cc/-configurationContexts argument containing a global configuration (stream) context URL.
Note: This command can be destructive.  As such, isolate the resources and target resources updated by this command (see the -iru/-ignoreResourceUrls, -pru/-processResourceUrls, -itru/-ignoreTargetResourceUrls, -ptru/-processTargetResourceUrls, -lm/-lastModified, -ri/-resourceIdentifier, -rt/-resourceTypes, and -ppn/-propertyPrefixedNames arguments).  In addition, perform a practice cleaning (see the -t/-test argument) and verify the results (see the -o/-output argument).
Support:
	
Resource                      | Property                               | Configuration Context
----------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | Yes
oslc_qm:TestPlan              | oslc_qm:relatedChangeRequest           | No
oslc_qm:TestPlan              | calm:testsDevelopmentPlan              | No
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | Yes
oslc_qm:TestCase              | oslc_qm:relatedChangeRequest           | No
oslc_qm:TestCase              | oslc_qm:testsChangeRequest             | No
oslc_qm:TestScript            | oslc_qm:relatedChangeRequest           | No
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | Yes
oslc_qm:TestExecutionRecord   | oslc_qm:blockedByChangeRequest         | No
oslc_qm:TestExecutionRecord   | oslc_qm:relatedChangeRequest           | No
oslc_qm:TestResult            | oslc_qm:affectedByChangeRequest        | No
oslc_qm:TestResult            | oslc_qm:relatedChangeRequest           | No
rqm_qm:TestScriptStepResult   | oslc_qm:affectedByChangeRequest        | No
rqm_qm:TestScriptStepResult   | oslc_qm:relatedChangeRequest           | No
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | No
oslc_rm:Requirement           | oslc_rm:validatedBy                    | No

Command: removeBrokenForwardLinksWithMissingBackLinks
Description: Removes from QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument all properties in the (optional) -ppn/-propertyPrefixedNames argument containing broken forward links (including associated forward link labels) to target RM (oslc_rm), CM (oslc_cm), and/or QM (oslc_qm) resources are missing the associated back links.  Supports the -pl/-propertyLabels (project areas enabled for configuration management only), -rt/-resourceTypes, and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.
Cause: When back links are removed from target RM (oslc_rm), CM (oslc_cm), and/or QM (oslc_qm) resources, the properties containing forward links are not removed from the QM (oslc_qm) and/or RM (oslc_rm) resources.
Note: This command supports project areas enabled for configuration management for only QM (oslc_qm) resources containing forward links to target RM (oslc_rm) resources (see Support table), which requires the -cc/-configurationContexts argument containing a global configuration (stream) context URL.
Support:
	
Resource                      | Property                               | Target Resource               | Target Property                        | Configuration Context
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | Yes*
oslc_qm:TestPlan              | oslc_qm:relatedChangeRequest           | oslc_cm:ChangeRequest         | oslc_cm:relatedTestPlan                | No
oslc_qm:TestPlan              | calm:testsDevelopmentPlan              | oslc_cm:ChangeRequest         | calm:testedByTestPlan                  | No
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy                    | Yes*
oslc_qm:TestCase              | oslc_qm:relatedChangeRequest           | oslc_cm:ChangeRequest         | oslc_cm:relatedTestCase                | No
oslc_qm:TestCase              | oslc_qm:testsChangeRequest             | oslc_cm:ChangeRequest         | oslc_cm:testedByTestCase               | No
oslc_qm:TestScript            | oslc_qm:relatedChangeRequest           | oslc_cm:ChangeRequest         | oslc_cm:relatedTestScript              | No
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy                    | Yes*
oslc_qm:TestExecutionRecord   | oslc_qm:blockedByChangeRequest         | oslc_cm:ChangeRequest         | oslc_cm:blocksTestExecutionRecord      | No
oslc_qm:TestExecutionRecord   | oslc_qm:relatedChangeRequest           | oslc_cm:ChangeRequest         | oslc_cm:relatedTestExecutionRecord     | No
oslc_qm:TestResult            | oslc_qm:affectedByChangeRequest        | oslc_cm:ChangeRequest         | oslc_cm:affectsTestResult              | No
oslc_qm:TestResult            | oslc_qm:relatedChangeRequest           | oslc_cm:ChangeRequest         | rtc_cm:relatedTestResult               | No
rqm_qm:TestScriptStepResult   | oslc_qm:affectedByChangeRequest        | oslc_cm:ChangeRequest         | oslc_cm:affectsTestResult              | No
rqm_qm:TestScriptStepResult   | oslc_qm:relatedChangeRequest           | oslc_cm:ChangeRequest         | rtc_cm:relatedTestResult               | No
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | No
oslc_rm:Requirement           | oslc_rm:validatedBy                    | oslc_qm:TestCase              | oslc_qm:validatesRequirement           | No

* Support for project areas enabled for configuration management uses unsupported techniques to resolve links from the application that does not own (or store/expose via OSLC) the links.  Links are resolved from the XHTML compact rendering/rich hover of the resource.  The XHTML compact rendering/rich hover of the resource is difficult to parse, locale-sensitive (supports English locale only or requires user-specified property labels), and may not work if the XHTML compact rendering/rich hover format changes.  As such, requires specifying the -al/-acceptLanguage argument with the en_US language value and/or specifying the locale-specific property labels for the links in the XHTML compact rendering/rich hover of the resource in the -pl/-propertyLabels argument.  For IBM Rational DOORS Next Generation, use the -updi/-usePrivateDngApi argument to resolve links using a private IBM Rational DOORS Next Generation API to work-around these limitations and another limitation of IBM Rational DOORS Next Generation, where the XHTML compact rendering/rich hover of the resource contains at most 15 links.

Command: reportBrokenLinks
Description: Reports the QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument that have properties in the (optional) -ppn/-propertyPrefixedNames argument containing broken links to target RM (oslc_rm) and QM (oslc_qm) resources.  Broken links exist in a RM (oslc_rm) or QM (oslc_qm) resource when the linked QM (oslc_qm) or RM (oslc_rm) resource does not contain a corresponding link to the RM (oslc_rm) or QM (oslc_qm) resource.  Supports the -pl/-propertyLabels (project areas enabled for configuration management only), -rt/-resourceTypes, and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.  If the -o/-output argument is specified, broken links are written to the console, when first found.  A report of all broken links (sorted) is written to the console after the execution completes.    
Cause: When links are not removed correctly from QM (oslc_qm) and/or RM (oslc_rm) resources, broken links exist in target RM (oslc_rm) and/or QM (oslc_qm) resources and broken links are difficult to detect manually.
Note: This command supports project areas enabled for configuration management for only QM (oslc_qm) and RM (oslc_rm) resources containing links to target RM (oslc_rm) and QM (oslc_qm) resources (see Support table), which requires the -cc/-configurationContexts argument containing a global configuration (stream or baseline) context URL.
Support:
	
Resource                      | Property                               | Target Resource               | Target Property                        | Configuration Context
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | Yes*
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy                    | Yes*
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy                    | Yes*
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | Yes*
oslc_rm:Requirement           | oslc_rm:validatedBy                    | oslc_qm:TestCase              | oslc_qm:validatesRequirement           | Yes*

* Support for project areas enabled for configuration management uses unsupported techniques to resolve links from the application that does not own (or store/expose via OSLC) the links.  Links are resolved from the XHTML compact rendering/rich hover of the resource.  The XHTML compact rendering/rich hover of the resource is difficult to parse, locale-sensitive (supports English locale only or requires user-specified property labels), and may not work if the XHTML compact rendering/rich hover format changes.  As such, requires specifying the -al/-acceptLanguage argument with the en_US language value and/or specifying the locale-specific property labels for the links in the XHTML compact rendering/rich hover of the resource in the -pl/-propertyLabels argument.  For IBM Rational DOORS Next Generation, use the -updi/-usePrivateDngApi argument to resolve links using a private IBM Rational DOORS Next Generation API to work-around these limitations and another limitation of IBM Rational DOORS Next Generation, where the XHTML compact rendering/rich hover of the resource contains at most 15 links.

Command: addMissingBackLinks
Description: Adds to target RM (oslc_rm) and/or QM (oslc_qm) resources all target properties containing missing back links (including associated back link labels - <QM resource title> for oslc_qm:TestPlan and oslc_qm:TestCase resources, <QM resource numerical ID>: <QM resource title> [<QM test script step number>] for rqm_qm:TestScriptStep resources, and <RM resource title> for oslc_rm:RequirementCollection and oslc_rm:Requirement resources) to QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument and properties in the (optional) -ppn/-propertyPrefixedNames argument.  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.
Cause: When QM (oslc_qm) and/or RM (oslc_rm) resources are copied, the target properties containing back links are not added to the target RM (oslc_rm) and/or QM (oslc_qm) resources.
Note: This command does NOT support project areas enabled for configuration management.
Support:
	
Resource                      | Property                               | Target Resource               | Target Property
-----------------------------------------------------------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | oslc_rm:RequirementCollection | oslc_rm:validatedBy
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection
oslc_rm:Requirement           | oslc_rm:validatedBy                    | oslc_qm:TestCase              | oslc_qm:validatesRequirement

Command: updateLinkLabels
Description: Removes from QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument all properties in the (optional) -ppn/-propertyPrefixedNames argument containing default link labels for target RM (oslc_rm) (<target RM resource URL>) and/or QM (oslc_qm) resources (<target QM resource URL>) and re-adds the properties to the QM (oslc_qm) and/or RM (oslc_rm) resources with the correct link labels for target RM (oslc_rm) resources (<target RM resource numerical ID>: <target RM resource title> for IBM Rational DOORS Next Generation and <target RM resource's module title> (<target RM resource numerical ID>) for IBM Rational DOORS and IBM Rational DOORS Web Access) and/or RM (oslc_rm) resources (<target QM resource numerical ID>: <target QM resource title>).  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.  
Cause: When target RM (oslc_rm) and/or QM (oslc_qm) resources are linked to QM (oslc_qm) and/or RM (oslc_rm) resources without link labels, the link labels for target RM (oslc_rm) and/or QM (oslc_qm) resources default to the target RM (oslc_rm) and/or QM (oslc_qm) resource URLs.
Note: This command supports project areas enabled for configuration management for only QM (oslc_qm) resources containing forward links to target RM (oslc_rm) resources (see Support table), which requires the -cc/-configurationContexts argument containing a global configuration (stream) context URL.
Support:
	
Resource                      | Property                               | Target Resource               | Configuration Context
------------------------------------------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | oslc_rm:RequirementCollection | Yes
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | Yes
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | Yes
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | oslc_qm:TestPlan              | No
oslc_rm:Requirement           | oslc_rm:validatedBy                    | oslc_qm:TestCase              | No

Command: updateForwardLinkLabels
Description: Removes from QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument all properties in the (optional) -ppn/-propertyPrefixedNames argument containing incorrect forward link labels for target RM (oslc_rm) and/or QM (oslc_qm) resources and re-adds the properties to the QM (oslc_qm) and RM (oslc_rm) resources with the correct link labels for target RM (oslc_rm) resources (<target RM resource numerical ID>: <target RM resource title> for IBM Rational DOORS Next Generation and <target RM resource's module title> (<target RM resource numerical ID>) for IBM Rational DOORS and IBM Rational DOORS Web Access) and/or QM (oslc_qm) resources (<target QM resource numerical ID>: <target QM resource title>).  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.  
Cause: When target RM (oslc_rm) and/or QM (oslc_qm) resources are updated (RM (oslc_rm) and/or QM (oslc_qm) resource title/heading and/or target RM (oslc_rm) resource module prefix), the forward link labels for target RM (oslc_rm) and/or QM (oslc_qm) resources become stale.  
Note: This command supports project areas enabled for configuration management for only QM (oslc_qm) resources containing forward links to target RM (oslc_rm) resources (see Support table), which requires the -cc/-configurationContexts argument containing a global configuration (stream) context URL.
Support:
	
Resource                      | Property                               | Target Resource               | Configuration Context
-----------------------------------------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | oslc_rm:RequirementCollection | Yes
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | Yes
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | Yes
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | oslc_qm:TestPlan              | No
oslc_rm:Requirement           | oslc_rm:validatedBy                    | oslc_qm:TestCase              | No

Command: updateBackLinkLabels
Description:  For QM (oslc_qm) and/or RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument and properties in the (optional) -ppn/-propertyPrefixedNames argument, removes from target RM (oslc_rm) and/or QM (oslc_qm) resources all target properties containing incorrect back link labels and re-adds the target properties to the target RM (oslc_rm) and/or QM (oslc_qm) resources with the correct back link labels (<QM resource numerical ID>: <QM resource title> for oslc_qm:TestPlan and oslc_qm:TestCase resources, <QM resource numerical ID>: <QM resource title> [<QM test script step number>] for rqm_qm:TestScriptStep resources, and <RM resource numerical ID>: <RM resource title> for oslc_rm:RequirementCollection and oslc_rm:Requirement resources).  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.
Cause: When QM (oslc_qm) and/or RM (oslc_rm) resources linked to target RM (oslc_rm) and/or QM (oslc_qm) resources are updated (QM (oslc_qm) test script step order and/or QM (oslc_qm) and/or RM (oslc_rm) resource name), the back link labels become stale.  In addition, older versions of IBM Rational Quality Manager, IBM Rational DOORS Next Generation, and/or IBM Rational DOORS and IBM Rational DOORS Web Access do not include the <QM resource numerical ID> or <RM resource numerical ID> prefix in the back link label.
Note: This command does NOT support project areas enabled for configuration management.
Support:
	
Resource                      | Property                               | Target Resource               | Target Property
-----------------------------------------------------------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | oslc_rm:RequirementCollection | oslc_rm:validatedBy
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | oslc_rm:Requirement           | oslc_rm:validatedBy
oslc_rm:RequirementCollection | oslc_rm:validatedBy                    | oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection
oslc_rm:Requirement           | oslc_rm:validatedBy                    | oslc_qm:TestCase              | oslc_qm:validatesRequirement

Note: The IBM Rational DOORS Web Access OSLC Requirements Management (RM) V2 API does not expose the back link label in the requirement RDF/XML.   As such, there is no way to determine the current back link label.  As a work-around, all back link labels are updated.  As such, the OSLC Cleaner Utility will take longer to run since it is updating all back link labels and correct back link labels will be updated resulting in modified requirements (included in requirement reconciliation) and history events with no changes. 

Command: updateVersionedLinks
Description: Removes from RM (oslc_rm) resources specified in the (optional) -rt/-resourceTypes argument all properties in the (optional) -ppn/-propertyPrefixedNames argument containing OSLC versioned URL forward links to target QM (oslc_qm) resources and if the OSLC concept URLs do not exist in the RM (oslc_rm) resources, re-adds the properties to the RM (oslc_rm) resources with the OSLC concept URLs for target QM (oslc_qm) resources.  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The RM (oslc_rm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.
Cause: When RM (oslc_rm) resources are updated (manually or via an external utility) with properties containing OSLC versioned URL forward links to target QM (oslc_qm) resources.
Note: This command does NOT support project areas enabled for configuration management.
Support:
	
Resource                      | Property           
---------------------------------------------------
oslc_rm:RequirementCollection | oslc_rm:validatedBy
oslc_rm:Requirement           | oslc_rm:validatedBy

Command: renameLinks
Description: Updates in QM (oslc_qm) resources in the (optional) -rt/-resourceTypes argument all properties in the (optional) -ppn/-propertyPrefixedNames argument containing forward links to target CM (oslc_cm) resources by renaming the old public URL in -opu/-oldPublicUrl (case-sensitive) to the new public URL in -npu/-newPublicUrl (case-sensitive).  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.
Cause: When target CM (oslc_cm) resources are moved/unavailable, the forward links are invalid.  
Note: This command does NOT support project areas enabled for configuration management.
Support:
	
Resource                      | Property
---------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:relatedChangeRequest
oslc_qm:TestPlan              | calm:testsDevelopmentPlan
oslc_qm:TestCase              | oslc_qm:relatedChangeRequest
oslc_qm:TestCase              | oslc_qm:testsChangeRequest
oslc_qm:TestScript            | oslc_qm:relatedChangeRequest
oslc_qm:TestExecutionRecord   | oslc_qm:blockedByChangeRequest
oslc_qm:TestExecutionRecord   | oslc_qm:relatedChangeRequest
oslc_qm:TestResult            | oslc_qm:affectedByChangeRequest
oslc_qm:TestResult            | oslc_qm:relatedChangeRequest
rqm_qm:TestScriptStepResult   | oslc_qm:affectedByChangeRequest
rqm_qm:TestScriptStepResult   | oslc_qm:relatedChangeRequest

Command: removeLinkConfigContext
Description: Fixes links in QM (oslc_qm) resources specified in the -rt/-resourceTypes argument that have properties in the -ppn/-propertyPrefixedNames argument and re-add's link labels for the new updated link (<RM resource numerical ID>: <RM resource title> for oslc_rm:RequirementCollection and oslc_rm:Requirement resources).  Supports the -rt/-resourceTypes and -ppn/-propertyPrefixedNames arguments. Broken links exist in QM (oslc_qm) resource when the linked RM (oslc_rm) resource contains suffix ?oslc_config.context or any query parameter. The QM (oslc_qm) resource types Supports the -rt/-resourceTypes, and -ppn/-propertyPrefixedNames arguments.  The QM (oslc_qm) resource types are used, if the -rt/-resourceTypes argument is not specified.  All properties are used, if the -ppn/-propertyPrefixedNames argument is not specified.  If the -o/-output argument is specified, links are written to the console, when first found.  A report of all problem links (sorted) is written to the console after the execution completes.    
Cause: If the RM (oslc_rm) resource(s) link is added to QM (oslc_qm) resource(s) with suffix ?oslc_config.context (configuration context included in the URI or any query parameter) using the ETM Reportable REST API or OSLC QM API, the QM (oslc_qm) resource(s) link does not exist in target RM (oslc_rm) resources. This symptom only occurs for configuration management enabled project areas. 
Note: This command supports project areas enabled for configuration management for only QM (oslc_qm) containing links to target RM (oslc_rm) resources (see Support table), which requires the -cc/-configurationContexts argument containing a global or local stream/baseline context URL.
Support:
	
Resource                      | Property                               | Configuration Context
----------------------------------------------------------------------------------------------
oslc_qm:TestPlan              | oslc_qm:validatesRequirementCollection | Yes
oslc_qm:TestCase              | oslc_qm:validatesRequirement           | Yes
rqm_qm:TestScriptStep         | oslc_qm:validatesRequirement           | Yes

Command: readTrs2
Description: Reads the Tracked Resource Set version 2 (TRS2) feeds from the server for the TRS2 provider types specified in the (optional) -t2pt/-trs2ProviderTypes argument and outputs the feed page(s) as RDF/XML to the console or file(s) under an output directory.  Includes reading the base and change log (no selections for project areas enabled for configuration management).  If an output directory is specified, a directory named 'TRS2' is created (or deleted and recreated, if exists) under the output directory.  The lqe_user or jts_user user (requires the JazzUsers repository permission and 'TRS Consumer-Internal' license) may be used to run this command.  If using another user, that user must have the (singleton) 'TRS Consumer-Internal' license assigned.  Before executing this command, unassign the 'TRS Consumer-Internal' license from the lqe_user or jts_user user and assign it to the user.  After executing this command, unassign the 'TRS Consumer-Internal' license from the user and reassign it to the lqe_user or jts_user user.  Supports the -od/-outputDirectory argument.  Supports the -t2pt/-trs2ProviderTypes argument.  All TRS2 provider types are used, if the -t2pt/-trs2ProviderTypes argument is not specified.  
Cause: When reading the TRS2 feeds, the base and change log are paged, possibly requiring multiple HTTP GET requests.

Command: readTrs2WithSelections
Description: Reads the Tracked Resource Set version 2 (TRS2) feeds from the server for the TRS2 provider types specified in the (optional) -t2pt/-trs2ProviderTypes argument and outputs the feed page(s) as RDF/XML to the console or file(s) under an output directory.  Includes reading the base, change log, and selections (for project areas enabled for configuration management).  If an output directory is specified, a directory named 'TRS2' is created (or deleted and recreated, if exists) under the output directory.  The lqe_user or jts_user user (requires the JazzUsers repository permission and 'TRS Consumer-Internal' license) may be used to run this command.  If using another user, that user must have the (singleton) 'TRS Consumer-Internal' license assigned.  Before executing this command, unassign the 'TRS Consumer-Internal' license from the lqe_user or jts_user user and assign it to the user.  After executing this command, unassign the 'TRS Consumer-Internal' license from the user and reassign it to the lqe_user or jts_user user.  Supports the -od/-outputDirectory argument.  Supports the -t2pt/-trs2ProviderTypes argument.  All TRS2 provider types are used, if the -t2pt/-trs2ProviderTypes argument is not specified.  
Cause: When reading the TRS2 feeds, the base, change log, and selections are paged, possibly requiring multiple HTTP GET requests.
Note: This command may be long-running and apply additional load on the server when there are many project areas enabled for configuration management and/or many configurations (streams and baselines) since reading the selections requires building the selections feed on-demand. 

Command: validateTrs2
Description: Validates the Tracked Resource Set version 2 (TRS2) feeds from the server for the TRS2 provider types specified in the (optional) -t2pt/-trs2ProviderTypes argument or input directory.  Note, the input directory must contain the same directory structure and RDF/XML file(s) as generated by the readTrs2 command or downloaded/uncompressed for data source(s) in Lifecycle Query Engine (LQE)/Link Index Provider (LDX) V6.0.5+.  Validation includes:

-Reading the TRS2 feeds including all pages.
-Validating the TRS2 change log (TRS2 change events) including event ordering and required/optional properties.  
-Validating the TRS2 base (TRS2 members and cutoff event) including required/optional properties.
-If any project area is enabled for configuration management, validate the TRS2 selections in the TRS2 base and TRS2 change log including the selections response ETag each selects and RDF patch events in the TRS2 change log including patch content and before/after eTags
-Simulated Lifecycle Query Engine (LQE)/Link Index Provider (LDX) indexing the TRS2 base and change log from the TRS2 cutoff event including validating/logging skipped resources.

All issues with the TRS2 feeds will be logged to the console.  The user must have the (singleton) 'TRS Consumer-Internal' license assigned.  Before executing this command, unassign the 'TRS Consumer-Internal' license from the lqe_user or jts_user user and assign it to the user.  After executing this command, unassign the 'TRS Consumer-Internal' license from the user and reassign it to the lqe_user or jts_user user.  Note, this command is memory intensive if the TRS2 feeds are large.  Supports the -id/-inputDirectory argument.  Supports the -t2pt/-trs2ProviderTypes argument when not using the -id/-inputDirectory argument.  All TRS2 provider types are used, if the -t2pt/-trs2ProviderTypes argument is not specified.  
Cause: When consumers (e.g LQE or LDX) of the TRS2 feeds report errors, the TRS2 feeds may have issues, causing the errors.

Command: validateTrs2NoSkippedResources
Description: Tracked Resource Set version 2 (TRS2) feeds from the server for the TRS2 provider types specified in the (optional) -t2pt/-trs2ProviderTypes argument or input directory.  Note, the input directory must contain the same directory structure and RDF/XML file(s) as generated by the readTrs2 command or downloaded/uncompressed for data source(s) in Lifecycle Query Engine (LQE)/Link Index Provider (LDX) V6.0.5+.  This command is the same as the validateTrs2 command except skipped resources are NOT validated/logged.  Use this command to quickly validate the TRS2 feeds without HTTP GET requests for all referenced artifacts/shapes in the TRS2 feeds.  Validation includes:

-Reading the TRS2 feeds including all pages.
-Validating the TRS2 change log (TRS2 change events) including event ordering and required/optional properties.  
-Validating the TRS2 base (TRS2 members and cutoff event) including required/optional properties.
-If any project area is enabled for configuration management, validate the RDF patch events in the TRS2 change log including patch content and before/after eTags.
-Simulated Lifecycle Query Engine (LQE)/Link Index Provider (LDX) indexing the TRS2 base and change log from the TRS2 cutoff event.  Note, skipped resources are NOT validated/logged.

All issues with the TRS2 feeds will be logged to the console.  The lqe_user or jts_user user (requires the JazzUsers repository permission and 'TRS Consumer-Internal' license) may be used to run this command.  If using another user, that user must have the (singleton) 'TRS Consumer-Internal' license assigned.  Before executing this command, unassign the 'TRS Consumer-Internal' license from the lqe_user or jts_user user and assign it to the user.  After executing this command, unassign the 'TRS Consumer-Internal' license from the user and reassign it to the lqe_user or jts_user user.  Note, this command is memory intensive if the TRS2 feeds are large.  Supports the -id/-inputDirectory argument.  Supports the -t2pt/-trs2ProviderTypes argument when not using the -id/-inputDirectory argument.  All TRS2 provider types are used, if the -t2pt/-trs2ProviderTypes argument is not specified.  
Cause: When consumers (e.g LQE or LDX) of the TRS2 feeds report errors, the TRS2 feeds may have issues, causing the errors.
		
Compatibility
=============
The OSLC Cleaner Utility is compatible with the following products:

    * IBM Rational Quality Manager 3.0.1 or later
    * IBM Rational Team Concert 3.0.1 or later
    * IBM Rational DOORS Next Generation 3.0.1 or later
    * IBM Rational DOORS and IBM Rational DOORS Web Access 9.5.0 or later
    * IBM Rational Software Architect Design Manager and IBM Rational Rhapsody Design Manager (Design Management) 6.0 or later

Note: Product names have changed in the 7.0 release (2Q2019):

    * IBM Rational Quality Manager --> IBM Engineering Test Management
    * IBM Rational Team Concert --> IBM Engineering Workflow Management 
    * IBM Rational DOORS Next Generation --> IBM Engineering Requirements Management DOORS Next
    * IBM Rational DOORS --> IBM Engineering Requirements Management DOORS
    * IBM Rational Rhapsody --> IBM Engineering Systems Design Rhapsody
    
However, the OSLC Cleaner Utility documentation references the old product names since the OSLC Cleaner Utility is backwards compatible.
    
Note: IBM Rational Software Architect Design Manager and IBM Rational Rhapsody Design Manager (Design Management) projects can be configured to act as an OSLC RM V2 service provider.  The OSLC Cleaner Utility only supports projects configured to act as an OSLC RM V2 service provider including RM (oslc_rm) resources and properties.

Note: Updating link labels (commands: updateLinkLabels and updateForwardLinkLabels) to/from RM resources in IBM Rational DOORS and IBM Rational DOORS Web Access requires IBM Rational DOORS and IBM Rational DOORS Web Access 9.7.0 or later (see IBM Rational DOORS and IBM Rational DOORS Web Access defect 11183).

Note: The rqm_qm:TestScriptStep resource is only supported in IBM Rational Quality Manager 4.0 and 4.0.6 or later.

Note: Logging options are changed as of ETM 7.0.3:
    * log4j2.xml is used for generating log files to capture utility execution output. 
    * cleanerUtility.log file captures logs generated when logging to a file is enabled in log4j2.xml.
    * The following arguments are removed:
    *     -l, -log
    *     -d, -debug
    *     -hd, -httpDebug
    *     -p, -performance    
The following changes are required in log4j2.xml in order to achieve the same level of logging for the removed arguments:
    * Argument -l, -log: Change logger com.ibm.rqm.cleaner to the DEBUG level and uncomment cleanerUtility appender. 
    * Argument -d, -debug: Change logger com.ibm.rqm.cleaner to the TRACE level and uncomment cleanerUtility appender.
    * Argument -hd, -httpDebug: Change logger org.apache.http to the TRACE level and uncomment cleanerUtility appender.
    * Argument -p, -performance: Change logger com.ibm.rqm.cleaner.internal.util.PerformanceUtils to the TRACE level and uncomment cleanerUtilityPerformance appender.

Recommended Usage Pattern
=========================

Before using this utility in production you should consider the following suggestions:

    * It is recommended to increase the log output verbose information settings in log4j2.xml to TRACE which includes the output of pre/post-updated resources (RDF/XML), useful for debugging any failures in the cleaning and restoring updated resources.
    * Perform a practice cleaning (see the -t/-test argument) and verify the results (see the -o/-output argument).
    * Requires Java 11 (IBM Java 11.0.17 Semeru Runtime) (<java.home>).
    * Requires a local key (consumer key and OAuth secret) to be created in IBM Rational DOORS (File >> OSLC >> Local Keys or File >> Local Keys or View >> Database View >> <right-click the database> >> Properties >> Local Keys or search the IBM Rational DOORS documentation for 'Local Keys') when IBM Rational Quality Manager is integrated with IBM Rational DOORS.  Note, the IBM Rational DOORS server should be restarted after creating the OAuth consumer key.
    * If an OutOfMemoryError is thrown (e.g. memory intensive commands), increase the amount of available JVM memory by adding the -Xmxnm argument where n is the maximum amount of available memory (MB) in multiples of 1024 (recommendation: 2048 MB or 2 GB).
    * When running from a Windows command prompt, some special characters (e.g. ( ) = ; , ` ' % " * ? & \ < > ^ |) in the command are interpreted by the command prompt and require escaping with a caret character (e.g. ^( ^) ^= ^; ^, ^` ^' ^% ^" ^* ^? ^& ^\ ^< ^> ^^ ^|).
    * When running from an Unix shell, some special characters (e.g. ~ ` # $ & * ( ) \ | [ ] { } ; ' " < > / ? *) in the command are interpreted by the shell and require escaping with a backslash character (e.g. \~ \` \# \$ \& \* \( \) \\ \| \[ \] \{ \} \; \' \" \< \> \/ \? \*).
    * Attempting to start the OSLC cleaner utility when TLS 1.2 is used and the -Dcom.ibm.team.repository.transport.client.protocol="TLSv1.2" argument is not set will result in a connection handshake failure. If using TLS 1.2, ensure the -Dcom.ibm.team.repository.transport.client.protocol="TLSv1.2" argument is included in order to ensure a successful connection.
    
Additional Resources 
====================

Additional resources for further reading:

    * OSLC Cleaner Utility WIKI (https://jazz.net/wiki/bin/view/Main/RqmOslcQmV2Api#OSLC_Cleaner_Utility)
    * OSLC Quality Management (QM) V2 resources (http://open-services.net/bin/view/Main/QmSpecificationV2#QM_Resource_Definitions) 
    * OSLC Quality Management (QM) V2 Service Provider Catalog (see http://open-services.net/bin/view/Main/OslcCoreSpecification?sortcol=table;up=#Service_Provider_Resources)
    * OSLC Requirements Management (RM) V2 resources (http://open-services.net/bin/view/Main/RmSpecificationV2?sortcol=table;up=#RM_Resource_Definitions)
    * OSLC Change Management (CM) V2 resources (http://open-services.net/bin/view/Main/CmSpecificationV2?sortcol=table;up=#CM_Resource_Definitions)
    * W3C/ISO8601 date/time (http://www.w3.org/TR/NOTE-datetime)
    * How to verify link consistency between RQM and DOORS 9 (https://jazz.net/wiki/bin/view/Deployment/HowToVerifyLinkConsistencyBetweenRQMAndDOORS9)
    * Ability to see back link status in traceability view (https://jazz.net/downloads/rational-quality-manager/releases/4.0.1?p=news#back_link)

Argument Reference
==================

Note: Argument values containing whitespace must be enclosed in double quote characters.

<java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -h

-h, -help
    Prints this help message.

<java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -v

-v, -version
    Prints the version of the Cleaner Utility.

<java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -lr

<java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -lt2pt

-lr, -listResources
    Lists the supported resources of the OSLC Cleaner Utility.

-lp, -listProperties
    Lists the supported properties of the OCLC Cleaner Utility.

-lt2pt, -listTrs2ProviderTypes
    Lists the supported Tracked Resource Set version 2 (TRS2) provider type of the Cleaner Utility.

-c, -commands=<command>
    A single command or list of commands (see above for supported commands).
    The list of commands is a single comma-separated (no whitespace) list: <command 1>,<command 2>,<command 3>

-qm, -qualityManagerURL=<URL>
    The fully qualified URL of IBM Rational Quality Manager.
    When a IBM Rational Quality Manager URL is specified, the following format is required:
    https://<server>:<port>/<context root>
    Note: A trailing forward slash character is optional.
    Note: The fully qualified URL is case-sensitive.

-rm, -requirementsManagerURL=<URL>
    The fully qualified URL of IBM Rational DOORS Next Generation, IBM Rational DOORS and IBM Rational DOORS Web Access, or IBM Rational Software Architect Design Manager and IBM Rational Rhapsody Design Manager (Design Management).
    When a IBM Rational DOORS Next Generation, IBM Rational DOORS and IBM Rational DOORS Web Access, or IBM Rational Software Architect Design Manager and IBM Rational Rhapsody Design Manager (Design Management) URL is specified, the following format is required:
    https://<server>:<port>/<context root>
    Note: A trailing forward slash character is optional.
    Note: The fully qualified URL is case-sensitive.
	
-cr, -credentials=<credential>
    A single credential or list of credentials for the supported products.
    When a credential is specified, the following format is required:
    <fully qualified URL for the supported product>::<username for a valid user for the supported product>::<password for the username for the supported product>[::<consumer key for the supported product>::<OAuth secret for the consumer key for the supported product>]
    Note: The credential is case-sensitive.
    When a fully qualified URL for the supported product is specified, the following format is required:
    https://<server>:<port>/<context root>
    Note: A trailing forward slash character is optional.
    Note: The fully qualified URL is case-sensitive.
    The list of credentials is a single comma-separated (no whitespace) list: [<fully qualified URL for the supported product 1>::]<credential 1>,[<fully qualified URL for the supported product 2>::]<credential 2>,[<fully qualified URL for the supported product 3>::]<credential 3>

-pa, -projectAreas=<project area>
    [Optional] A single project area name or list of project area names.
    When multiple supported products are specified, the following format is required:
    <fully qualified URL for the supported product>::<project area name>
    When a fully qualified URL for the supported product is specified, the following format is required:
    https://<server>:<port>/<context root>
    Note: A trailing forward slash character is optional.
    Note: The fully qualified URL is case-sensitive.
    The list of project area names is a single comma-separated (no whitespace) list: [<fully qualified URL for the supported product 1>::]<project area name 1>,[<fully qualified URL for the supported product 2>::]<project area name 2>,[<fully qualified URL for the supported product 3>::]<project area name 3>
    When the -pa/-projectArea argument is omitted, the command processes all project areas resolved from the OSLC V2's Service Provider Catalog for each supported product.

-cc, -configurationContexts=<configuration context>
    [Optional] A single configuration context URL or list of configuration context URLs.
    When a configuration context URL is specified, the following format is required:
    <Configuration Management-enabled project area name>::<configuration context URL>
    When multiple supported products are specified, the following format is required:
    <fully qualified URL for the supported product>::<Configuration Management-enabled project area name>::<configuration context URL>
    When a fully qualified URL for the supported product is specified, the following format is required:
    https://<server>:<port>/<context root>
    Note: A trailing forward slash character is optional.
    Note: The fully qualified URL is case-sensitive.
    The list of configuration context URLs is a single comma-separated (no whitespace) list: [<fully qualified URL for the supported product 1>::]<Configuration Management-enabled project area name 1>::<configuration context URL 1>,[<fully qualified URL for the supported product 2>::]<Configuration Management-enabled project area name 2>::<configuration context URL 2>,[<fully qualified URL for the supported product 3>::]<Configuration Management-enabled project area name 3>::<configuration context URL 3>
    When the -cc/-configurationContexts argument is omitted, the default local configuration for each of the Configuration Management-enabled project area(s) is used.
    Note: The -cc/-configurationContexts argument is intended to be used for only Configuration Management-enabled project areas.
    Note: If the command interacts (e.g. reads) with target resources, the -cc/-configurationContexts argument MUST contain a global configuration (stream or baseline) context URL. 
    Note: The configuration context URL will be included in the Configuration-Context header and oslc_config_context request parameter for all requests to the associated/target Configuration Management-enabled project area(s).
    Note: The configuration context URL may contain a global configuration (stream or baseline) or local configuration (stream or baseline) context URL.  
    Note: If the configuration context URL represents a global configuration, the global configuration must contain local configuration contributions for each of the associated/target Configuration Management-enabled project area(s).
    Note: If the configuration context URL represents a baseline, artifacts in the associated/target Configuration Management-enabled project area(s) cannot be updated since baselines are immutable.
    Note: The configuration context URL can be copied from the Configuration Context menu (Current Configuration >> Global Configuration or Current Configuration >> <domain or application name> >> Local Configuration) in the associated product. 
    The -cc/-configurationContexts argument is not supported by all commands (see above) since links between artifacts in different applications that have project areas enabled for configuration management are stored/exposed (via OSLC) by the owning application. 

-rt, -resourceTypes=<resource type>
	[Optional] A single resource type or list of resource types.
	The -lr/-listResources argument lists the supported case-sensitive values.
	The list of resource types is a single comma-separated (no whitespace) list: <resource type 1>,<resource type 2>,<resource type 3>
    The -rt/-resourceTypes argument is not supported by all commands (see above).

-al, -acceptLanguage=<language>
	[Optional] The value of the Accept-Language HTTP request header, used with all HTTP requests.  For example, en-US for English.
	For more information, see https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4.
	Note: The -al/-acceptLanguage argument supports all Accept-Language HTTP request header values supported by the HTTP/1.1 specification.

-ppn, -propertyPrefixedNames=<property prefixed name>
	[Optional] A single property prefixed name or list of property prefixed names.
	The -lp/-listProperties argument lists the supported case-sensitive values.
	The list of property prefixed names is a single comma-separated (no whitespace) list: <property prefixed name 1>,<property prefixed name 2>,<property prefixed name 3>
    The -ppn/-propertyPrefixedNames argument is not supported by all commands (see above).

-pl, -propertyLabels=<property label>
	[Optional] A single property label or list of property labels.
    When a property label is specified, the following format is required:
    <property prefixed name>::<property label>
    Note: The property label is case-sensitive and locale-specific.
    When a property prefixed name is specified, the following format is required:
    <name space prefix>:<property name>
    Note: The property prefixed name is case-sensitive.
    The list of property labels is a single comma-separated (no whitespace) list: <property prefixed name 1>::<property label 1>,<property prefixed name 2>::<property label 2>,<property prefixed name 3>::<property label 3>
    Note: Multiple property labels may be defined for the same property prefixed name.  For example, support for multiple locales.  For example: <property prefixed name 1>::<property label 1>,<property prefixed name 1>::<property label 2>
    The -pl/-propertyLabels argument is not supported by all commands (see above).

-iru, -ignoreResourceUrls=<URL>
	[Optional] A single URL or list of URLs.
    The URL (partial or complete) of a resource that will be ignored.
    When a URL is specified, the following format is required:
    https://<server>:<port>/<context root>[/<path>]
    Note: The URL is case-sensitive.
    Note: The URL may contain a partial resource URL.

-pru, -processResourceUrls=<URL>
	[Optional] A single URL or list of URLs.
    The URL (partial or complete) of a resource that will be processed when found in a resource property.
    When a URL is specified, the following format is required:
    https://<server>:<port>/<context root>[/<path>]
    Note: The URL is case-sensitive.
    Note: The URL may contain a partial resource URL.
    Note: The -iru/-ignoreResourceUrls has precedence over the -pru/-processResourceUrls argument.  That is, if a resource is ignore, it cannot be processed.

-itru, -ignoreTargetResourceUrls=<URL>
	[Optional] A single URL or list of URLs.
    The URL (partial or complete) of a target resource that will be ignored when found in a resource property.
    When a URL is specified, the following format is required:
    https://<server>:<port>/<context root>[/<path>]
    Note: The URL is case-sensitive.
    Note: The URL may contain a partial target resource URL.

-ptru, -processTargetResourceUrls=<URL>
	[Optional] A single URL or list of URLs.
    The URL (partial or complete) of a target resource that will be processed when found in a resource property.
    When a URL is specified, the following format is required:
    https://<server>:<port>/<context root>[/<path>]
    Note: The URL is case-sensitive.
    Note: The URL may contain a partial target resource URL.
    Note: The -itru/-ignoreTargetResourceUrls has precedence over the -ptru/-processTargetResourceUrls argument.  That is, if a target resource is ignore, it cannot be processed.

-t2pt, -trs2ProviderTypes=<TRS2 provider type>
	[Optional] A single Tracked Resource Set version 2 (TRS2) provider type or list of TRS2 provider types.
	The -lt2pt/-listTrs2ProviderTypes argument lists the supported case-sensitive values.
	The list of TRS2 provider types is a single comma-separated (no whitespace) list: <TRS2 provider type 1>,<TRS2 provider type 2>,<TRS2 provider type 3>
    The -t2pt/-trs2ProviderTypes argument is not supported by all commands (see above).

-od, -outputDirectory=<directory>
    [Optional] Writes command output to the specified output directory (relative directory name or absolute directory path).
    When an output directory is not specified, command output is written to the console.  In addition, verbose execution and summary information is output to the console (see the -o/-output argument).
    Note: The output directory must exist.

-id, -inputDirectory=<directory>
    [Optional] Reads command input from the specified input directory (relative directory name or absolute directory path).
    The input directory must contain the expected directory structure and file(s), as documented by supporting commands. 
    Note: The input directory must exist.

-lm, -lastModified=<date/time or number of seconds>
    [Optional] The date/time or number of seconds since a resource was last modified.
    Only resources modified after (greater than) the last modified date/time are processed.
    When a date/time is specified, a W3C/ISO8601 date/time with second-level precision expressed in UTC (Coordinated Universal Time) using the 'Z' UTC designator (e.g. 2013-09-17T17:18:16Z) is required.
    When a number of seconds is specified, a positive numerical value (greater than 0) is required to calculate the last modified date/time from the date/time the OSLC Cleaner Utility is executed.
    Unit of Time | Number of Seconds
    --------------------------------
    1 week       | 604800
    1 day        | 86400
    1 hour       | 3600
    1 minute     | 60 

-ri, -resourceIdentifier=<resource identifier>
    [Optional] A single resource identifier (value of the dcterms:identifier property) or list of resource identifiers.
    Only resources with identifiers in the -ri/-resourceIdentifier argument are processed.
    The list of resource identifiers is a single comma-separated (no whitespace) list: <resource identifier 1>,<resource identifier 2>,<resource identifier 3>

-opu, -oldPublicUrl=<public URL>
    [Optional] The old public URL of a link before a rename.
    When a public URL is specified, the following format is required:
    <protocol or scheme>://<host name or IP address>[:<port>][/<context root>]
    Note: A trailing forward slash character is optional.
    Note: The public URL is case-sensitive.
    The -opu/-oldPublicUrl argument is not supported by all commands (see above).
	
-npu, -newPublicUrl=<public URL>
    [Optional] The new public URL of a link after a rename.
    When a public URL is specified, the following format is required:
    <protocol or scheme>://<host name or IP address>[:<port>][/<context root>]
    Note: A trailing forward slash character is optional.
    Note: The public URL is case-sensitive.
    The -npu/-newPublicUrl argument is not supported by all commands (see above).
	
-ire, -ignoreReadErrors
    [Optional] Ignores errors when reading an OSLC resource and continues execution of the OSLC Cleaner Utility.  
	Note: All errors are logged to the log file when com.ibm.rqm.cleaner is set to TRACE level in log4j2.xml.
	Note: Ignored read errors are logged to the console when using the -o/-output argument.
	
-o, -output
    [Optional] Outputs verbose execution and summary information to the console.

-ps, -pageSize=<page size>
    [Optional] Page size (oslc.pageSize query parameter) of all OSLC API queries.
    For more information, see https://archive.open-services.net/bin/view/Main/OslcCoreSpecification.html#Resource_Paging.
    Note: For internal use only to improve performance.
	Note: The -ps/-pageSize argument supports page sizes greater than zero.
	Note: The default page size (512) is used, if the -ps/-pageSize argument is not specified.
	Note: Increasing the page size may have a negative or unknown impact on performance.
	Note: Some OSLC APIs ignore or limit (unpublished) the page size.	 

-updi, -usePrivateDngApi
    [Optional] Uses a private IBM Rational DOORS Next Generation API to resolve links from IBM Rational DOORS Next Generation that IBM Rational DOORS Next Generation does not own (or store/expose via OSLC) when the project areas is enabled for configuration management.
    The -updi/-usePrivateDngApi argument is not supported by all commands (see above).

-ddqt, -disableDngQueryTimeout
    [Optional] Disables the Rogue Query Monitor in IBM Rational DOORS Next Generation for all HTTP requests.

-aru, -attemptRqmUnlock
    [Optional] When updating locked QM (oslc_qm) resources in IBM Rational Quality Manager, attempt to unlock/lock the QM (oslc_qm) resources using the ETM Reportable REST API before/after the update.
    Note: The -aru/-attemptRqmUnlock argument does not support e-signatures.  That is, project areas with the 'E-Signature Required for Lock/Unlock' precondition enabled for the QM (oslc_qm) resource type.

-t, -test
    [Optional] Executes the OSLC Cleaner Utility without updating resources.


Removed Arguments 
=================

-l, -log=<file>      
-d, -debug
-p, -performance
-hd, -httpDebug
    As of ETM 7.0.3, using these arguments result in error.
    The logging options are controlled via changes to the log4j2.xml file.


Usage Examples
==============

Report all oslc_qm:TestPlan and oslc_qm:TestCase resources and all supported properties containing broken links to target RM (oslc_rm) resources (ignoring links broken links to target RM (oslc_rm) resources in IBM Rational Software Architect Design Manager or IBM Rational Rhapsody Design Manager (Design Management)) from one QM project area named "Project A" in the global configuration at "https://myhost:9443/gc/configuration/1" with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=reportBrokenLinks -al=en_US -qm=https://myhost:9443/qm -cr=https://myhost:9443/qm::ADMIN::ADMIN,https://myhost:9443/rm::ADMIN::ADMIN -pa="https://myhost:9443/qm::Project A" -cc="https://myhost:9443/qm::Project A::https://myhost:9443/gc/configuration/1" -rt=oslc_qm:TestPlan,oslc_qm:TestCase -ppn=oslc_qm:validatesRequirement,oslc_qm:validatesRequirementCollection -pl="oslc_rm:validatedBy::Validated By" -itru=https://myhost:9443/dm -o

Report all oslc_rm:RequirementCollection and oslc_rm:Requirement resources and all supported properties containing broken links to target QM (oslc_qm) resources from one RM project area named "Project A" in the global configuration at "https://myhost:9443/gc/configuration/1" with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=reportBrokenLinks -al=en_US -rm=https://myhost:9443/rm -cr=https://myhost:9443/rm::ADMIN::ADMIN,https://myhost:9443/qm::ADMIN::ADMIN -pa="https://myhost:9443/rm::Project A" -cc="https://myhost:9443/rm::Project A::https://myhost:9443/gc/configuration/1" -rt=oslc_rm:RequirementCollection,oslc_rm:Requirement -ppn=oslc_rm:validatedBy -pl="oslc_rm:validatedBy::Validated By" -o

Read all oslc_qm:TestPlan resources from one QM project area named "Project A" and read all oslc_rm:RequirementCollection resources from one RM project area named "Project B", both in the global configuration at "https://myhost:9443/gc/configuration/1" and outputs the feed page(s) as RDF/XML to file(s) under an output directory with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=readAllResources -qm=https://myhost:9443/qm -cr=https://myhost:9443/qm::ADMIN::ADMIN,https://myhost:9443/rm::ADMIN::ADMIN -pa="https://myhost:9443/qm::Project A,https://myhost:9443/rm::Project B" -cc="https://myhost:9443/qm::Project A::https://myhost:9443/gc/configuration/1,https://myhost:9443/rm::Project B::https://myhost:9443/gc/configuration/1" -rt=oslc_qm:TestPlan,oslc_rm:RequirementCollection -od=output_directory -o

Clean all oslc_qm:TestPlan resources and all supported properties containing forward links to target RM (oslc_rm) and/or CM (oslc_cm) resources from one QM project area named "Project A" and oslc_rm:RequirementCollection resources from one RM project area named "Project B" with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=removeBrokenForwardLinks -qm=https://myhost:9443/qm -cr=https://myhost:9443/qm::ADMIN::ADMIN,https://myhost:9443/rm::ADMIN::ADMIN -pa="https://myhost:9443/qm::Project A,https://myhost:9443/rm::Project B" -rt=oslc_qm:TestPlan,oslc_rm:RequirementCollection  -o

Clean all oslc_qm:TestPlan resources and all supported properties containing forward links to target RM (oslc_rm) and/or CM (oslc_cm) resources from one project area named "Project A" with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=removeBrokenForwardLinks -qm=https://myhost:9443/qm -cr=https://myhost:9443/qm::ADMIN::ADMIN -pa="Project A" -rt=oslc_qm:TestPlan -o

Test clean all supported QM/RM resources and all supported properties containing forward links to target RM (oslc_rm), CM (oslc_cm), and/or QM (oslc_qm) resources from all project areas and target RM resources with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=removeBrokenForwardLinks -qm=https://myhost:9443/qm -cr=https://myhost:9443/qm::ADMIN::ADMIN,https://myhost:8443/dwa::DOORS_USER::DOORS_PASSWORD::DOORS_CONSUMER_KEY::DOORS_OAUTH_SECRET -o -t

Clean all supported QM resources and all supported properties containing forward links to target CM (oslc_cm) resources are missing the associated back links from one project area named "Project A" with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=removeBrokenForwardLinksWithMissingBackLinks -qm=https://myhost:9443/qm -cr=https://myhost:9443/qm::ADMIN::ADMIN,https://myhost:9443/ccm::ADMIN::ADMIN -pa="Project A" -rt=oslc_qm:TestPlan,oslc_qm:TestCase,oslc_qm:TestScript,oslc_qm:TestExecutionRecord,oslc_qm:TestResult,rqm_qm:TestScriptStepResult -ppn=oslc_qm:relatedChangeRequest,oslc_qm:testsChangeRequest,oslc_qm:blockedByChangeRequest,oslc_qm:affectedByChangeRequest,calm:testsDevelopmentPlan -o

Update all oslc_rm:Requirement resources and all supported properties containing OSLC versioned URL forward links to target QM (oslc_qm) resources from one RM project area named "Project A" with outputting verbose execution and summary information to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=updateVersionedLinks -rm=https://myhost:9443/rm -cr=https://myhost:9443/rm::ADMIN::ADMIN -pa="https://myhost:9443/rm::Project A" -rt=oslc_rm:Requirement -o

Reads the Tracked Resource Set version 2 (TRS2) for QM Resources (type: http://open-services.net/ns/qm#) and QM Process Resources (http://jazz.net/ns/process#) feeds from the server including the base and change log (no selections for project areas enabled for configuration management) and outputs the feed page(s) as RDF/XML to file(s) under an output directory:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=readTrs2 -qm=https://myhost:9443/qm/ -cr=https://myhost:9443/qm::lqe_user::lqe_user -t2pt=http://open-services.net/ns/qm#,http://jazz.net/ns/process# -od=output_directory 

Reads the Tracked Resource Set version 2 (TRS2) for QM Resources (type: http://open-services.net/ns/qm#) and QM Process Resources (http://jazz.net/ns/process#) feeds from the server including the base, change log, and selections (for project areas enabled for configuration management) and outputs the feed page(s) as RDF/XML to file(s) under an output directory:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=readTrs2WithSelections -qm=https://myhost:9443/qm/ -cr=https://myhost:9443/qm::lqe_user::lqe_user -t2pt=http://open-services.net/ns/qm#,http://jazz.net/ns/process# -od=output_directory 

Validates the Tracked Resource Set version 2 (TRS2) for QM Resources (type: http://open-services.net/ns/qm#) and QM Process Resources (http://jazz.net/ns/process#) feeds from the server including validating/logging skipped resources with logging issues with the TRS2 feeds to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=validateTrs2 -qm=https://myhost:9443/qm/ -cr=https://myhost:9443/qm::ADMIN::ADMIN -t2pt=http://open-services.net/ns/qm#,http://jazz.net/ns/process#

Validates the Tracked Resource Set version 2 (TRS2) feed(s) from an input directory including validating/logging skipped resources with logging issues with the TRS2 feeds to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=validateTrs2 -qm=https://myhost:9443/qm/ -cr=https://myhost:9443/qm::ADMIN::ADMIN -id=input_directory

Validates the Tracked Resource Set version 2 (TRS2) for QM Resources (type: http://open-services.net/ns/qm#) and QM Process Resources (http://jazz.net/ns/process#) feeds from the server with logging issues with the TRS2 feeds to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=validateTrs2NoSkippedResources -qm=https://myhost:9443/qm/ -cr=https://myhost:9443/qm::lqe_user::lqe_user -t2pt=http://open-services.net/ns/qm#,http://jazz.net/ns/process#

Validates the Tracked Resource Set version 2 (TRS2) feed(s) from an input directory with logging issues with the TRS2 feeds to the console:
    <java.home>/bin/java.exe -jar OSLCCleanerUtility.jar -c=validateTrs2NoSkippedResources -id=input_directory

Accessing the Source
====================

All source files for the utility are included in the 'src' directory of the RQMUCleanerUtility.jar file. In order to build the source, the libraries in the 'lib' directory of the JAR file are required.