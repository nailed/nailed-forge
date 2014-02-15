package jk_5.nailed.updater.json;

/**
 * No description given
 *
 * @author jk-5
 */
public class RemoteLibrary {

    public int rev;
    public String id;
    public String name;
    public String url = null;
    public String pattern = null;
    public String tweaker;
    private Artifact artifact = null;

    public String getPath(){
        if(this.artifact == null){
            this.artifact = new Artifact(this.name);
        }
        return this.artifact.getPath();
    }

    public String getArtifactName(){
        if(this.artifact == null){
            this.artifact = new Artifact(this.name);
        }
        return this.artifact.getArtifact();
    }

    public String getUrl(){
        if(this.artifact == null){
            this.artifact = new Artifact(this.name);
        }
        if(this.url != null){
            return this.url;
        }
        if(this.pattern != null){
            return this.pattern.replace("{VERSION}", this.artifact.version);
        }
        return null;
    }

    public String getFileUrl(){
        if(this.artifact == null){
            this.artifact = new Artifact(this.name);
        }
        if(this.url != null){
            String ret = this.url;
            if(!ret.endsWith("/")){
                ret += "/";
            }
            ret += this.getPath();
            return ret;
        }
        if(this.pattern != null){
            return this.pattern.replace("{VERSION}", this.artifact.version);
        }
        return null;
    }

    @Override
    public String toString(){
        return this.name;
    }

    private class Artifact {
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
