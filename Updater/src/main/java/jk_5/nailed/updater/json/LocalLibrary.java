package jk_5.nailed.updater.json;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class LocalLibrary {

    public LocalLibrary(String id, String name, String checksum, String tweaker, String[] classLoaderExclusions, String[] transformerExclusions, boolean launcher){
        this.id = id;
        this.name = name;
        this.checksum = checksum;
        this.tweaker = tweaker;
        this.classLoaderExclusions = classLoaderExclusions;
        this.transformerExclusions = transformerExclusions;
        this.launcher = launcher;
    }

    public String id;
    public String name;
    public String checksum;
    public String tweaker;
    public String[] classLoaderExclusions;
    public String[] transformerExclusions;
    public boolean launcher = true;
    private Artifact artifact = null;

    public Artifact getArtifact(){
        if(this.artifact == null){
            this.artifact = new Artifact(this.name);
        }
        return this.artifact;
    }

    public String getPath(){
        if(this.artifact == null){
            this.artifact = new Artifact(this.name);
        }
        return this.artifact.getPath();
    }

    @Getter
    public class Artifact {
        private String domain;
        private String name;
        private String version;
        private String classifier;
        private String ext = "jar";

        public Artifact(String rep){
            String[] pts = rep.split(":");
            int idx = pts[pts.length - 1].indexOf('@');
            if(idx != -1){
                ext = pts[pts.length - 1].substring(idx + 1);
                pts[pts.length - 1] = pts[pts.length - 1].substring(0, idx);
            }
            domain = pts[0];
            name = pts[1];
            version = pts[2];
            if(pts.length > 3) classifier = pts[3];
        }

        public String getArtifact(){
            return getArtifact(classifier);
        }

        public String getArtifact(String classifier){
            String ret = domain + ":" + name + ":" + version;
            if(classifier != null) ret += ":" + classifier;
            if(!"jar".equals(ext)) ret += "@" + ext;
            return ret;
        }

        public String getPath(){
            return getPath(classifier);
        }

        public String getPath(String classifier){
            String ret = String.format("%s/%s/%s/%s-%s", domain.replace('.', '/'), name, version, name, version);
            if(classifier != null) ret += "-" + classifier;
            return ret + "." + ext;
        }
    }
}
