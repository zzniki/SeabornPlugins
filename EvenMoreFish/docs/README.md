# EMF Documentation & Website

Built with [Docusaurus](https://docusaurus.io/).

## Quick Start

1. Install dependencies:
   ```bash
   yarn
   ```
   or
   ```bash
   npm install
   ```

2. Run locally:
   ```bash
   yarn start
   ```
   or
   ```bash
   npm run start
   ```

3. Build for production:
   ```bash
   yarn build
   ```
   or
   ```bash
   npm run build
   ```
   

## Debugging Issues
Sometimes git may bug out and cause the build folder to disappear. (Happens when switching branches).
In this case run: `git rm -r --cached docs/build` and the commit as usual.
If experiencing any additional issues, it's recommended to run `docusaurus clear` and rebuild as well.