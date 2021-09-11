"use strict";

function LsGitRepo() {
    this.owner;
    this.repo;
    this.ref;

    this.parse = (str) => {
        let res = str.split(":");
        this.owner = res[0];
        this.repo = res[1];
        this.ref = res[2];
        return this;
    }

    this.toString = () => {
        return `${this.owner}:${this.repo}:${this.ref}`;
    }
}
