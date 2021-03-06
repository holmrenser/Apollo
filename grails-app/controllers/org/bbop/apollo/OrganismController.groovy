package org.bbop.apollo

import org.apache.shiro.session.Session
import org.bbop.apollo.gwt.shared.FeatureStringEnum

import grails.converters.JSON
import org.apache.shiro.SecurityUtils
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class OrganismController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def sequenceService
    def permissionService
    def requestHandlingService


    @Transactional
    def deleteOrganism() {
        log.debug "DELETING ORGANISM params: ${params.data}"
        def organismJson = JSON.parse(params.data.toString()) as JSONObject
        log.debug "organismJSON ${organismJson}"
        log.debug "id: ${organismJson.id}"
        Organism organism = Organism.findById(organismJson.id as Long)
        if (organism) {
            UserOrganismPreference.deleteAll(UserOrganismPreference.findAllByOrganism(organism))
            organism.delete()
        }
        render findAllOrganisms()
    }

    @Transactional
    def saveOrganism() {
        log.debug "saving params: ${params.data}"
        def organismJson = JSON.parse(params.data.toString()) as JSONObject
        log.debug "organismJSON ${organismJson}"
        log.debug "id: ${organismJson.id}"
        Organism organism = new Organism(
                commonName: organismJson.commonName
                , directory: organismJson.directory
                , blatdb: organismJson.blatdb
                , species: organismJson.species
                , genus: organismJson.genus
        )
        log.debug "organism ${organism as JSON}"

        checkOrganism(organism)
        try {
            organism.save(failOnError: true, flush: true, insert: true)
            sequenceService.loadRefSeqs(organism)
        } catch (e) {
            log.error("problem saving organism: " + e)
        }


        render findAllOrganisms()
    }

    private boolean checkOrganism(Organism organism) {
        File directory = new File(organism.directory)
        File trackListFile = new File(organism.getTrackList())
        File refSeqFile = new File(organism.getRefseqFile())

        if (!directory.exists() || !directory.isDirectory()) {
            log.error("Is an invalid directory: " + directory.absolutePath)
            organism.valid = false
        } else if (!trackListFile.exists()) {
            log.error("Track file does not exists: " + trackListFile.absolutePath)
            organism.valid = false
        } else if (!refSeqFile.exists()) {
            log.error("Reference sequence file does not exists: " + refSeqFile.absolutePath)
            organism.valid = false
        } else if (!trackListFile.text.contains("WebApollo")) {
            log.error("Track is not WebApollo enabled: " + trackListFile.absolutePath)
            organism.valid = false
        } else {
            organism.valid = true
        }
        return organism.valid
    }


    @Transactional
    def updateOrganismInfo() {
        log.debug "updating organism info ${params}"
        log.debug "updating feature ${params.data}"
        def organismJson = JSON.parse(params.data.toString()) as JSONObject
        log.debug "jsonObject ${organismJson}"
        Organism organism = Organism.findById(organismJson.id)
        log.debug "found an organism ${organism}"
        if (organism) {
            organism.commonName = organismJson.name
            organism.blatdb = organismJson.blatdb
            organism.species = organismJson.species
            organism.genus = organismJson.genus

            boolean directoryChanged = organism.directory != organismJson.directory || organismJson.forceReload
            log.debug "directoryChanged ${directoryChanged}"
            try {
                if (directoryChanged) {
                    organism.directory = organismJson.directory
                }
                organism.save(flush: true, insert: false, failOnError: true)

                if (directoryChanged && checkOrganism(organism)) {
                    sequenceService.loadRefSeqs(organism)
                }
            } catch (e) {
                log.error("Problem updating organism info: ${e}")
            }
            render findAllOrganisms()
        } else {
            log.debug "organism not found ${organismJson}"
            render { text: 'NOT updated' } as JSON
        }
    }

    //

//    @Transactional
//    def switchAppOrganism(Organism organismInstance) {
////        log.debug "found the organism ${organism}"
////        session.setAttribute(FeatureStringEnum.ORGANISM_JBROWSE_DIRECTORY.value, organism.directory)
////        session.setAttribute(FeatureStringEnum.ORGANISM_ID.value, organism.id)
//
//        preferenceService.setCurrentOrganism(permissionService.currentUser,organismInstance)
//
////        render organism as JSON
//
//        render annotatorService.getAppState
//    }

    def findAllOrganisms() {

        def organismList = permissionService.getOrganismsForCurrentUser()
        UserOrganismPreference userOrganismPreference = UserOrganismPreference.findByUserAndCurrentOrganism(permissionService.currentUser, true)
        Long defaultOrganismId = userOrganismPreference ? userOrganismPreference.organism.id : null

//        request.session.getAttribute(FeatureStringEnum.ORGANISM_JBROWSE_DIRECTORY.value) == organism.directory

        log.debug "organism list: ${organismList}"

        log.debug "finding all organisms: ${Organism.count}"

        JSONArray jsonArray = new JSONArray()
        for (def organism in organismList) {
            log.debug "TEST123"
            log.debug organism
//            Integer geneCount = Gene.executeQuery("select count(distinct g) from Gene g join g.featureLocations fl join fl.sequence s join s.organism o where o.id=:organismId", ["organismId": organism.id])[0]

            Integer annotationCount = Feature.executeQuery("select count(distinct f) from Feature f left join f.parentFeatureRelationships pfr  join f.featureLocations fl join fl.sequence s join s.organism o  where f.childFeatureRelationships is empty and o = :organism and f.class in (:viewableTypes)", [organism: organism, viewableTypes: requestHandlingService.viewableAnnotationList])[0] as Integer
            JSONObject jsonObject = [
                    id             : organism.id,
                    commonName     : organism.commonName,
                    blatdb         : organism.blatdb,
                    directory      : organism.directory,
                    annotationCount: annotationCount,
                    sequences      : organism.sequences?.size(),
                    genus          : organism.genus,
                    species        : organism.species,
                    valid          : organism.valid,
                    currentOrganism: defaultOrganismId != null ? organism.id == defaultOrganismId : false
            ] as JSONObject
            jsonArray.add(jsonObject)
        }
        render jsonArray as JSON
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'organism.label', default: 'Organism'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

}
