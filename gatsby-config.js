/** @type {import('gatsby').GatsbyConfig} */
module.exports = {
  siteMetadata: {
    title: `Chainlist`,
    siteUrl: `https://evm-chainlist.netlify.app/`,
    author: "@frederikbolding",
    description:
      "A list of EVM-based chains that also allows you to add chains to your favorite Web3 wallet.",
  },
  plugins: [
    "gatsby-plugin-emotion",
    "gatsby-plugin-react-helmet",
    "gatsby-plugin-netlify",
    {
      // For `gatsby-transformer-json` to work, `gatsby-source-filesystem` needs to be loaded. This
      // plugin requires a specific folder to be set however, so here we just specify the pages
      // folder as a dummy folder.
      resolve: 'gatsby-source-filesystem',
      options: {
        name: 'pages',
        path: 'src/pages'
      }
    },
    "gatsby-transformer-json",
    {
      resolve: "@chakra-ui/gatsby-plugin",
      options: {
        /**
         * @property {boolean} [resetCSS=true]
         * if false, this plugin will not use `<CSSReset />
         */
        resetCSS: true,
        /**
         * @property {boolean} [isUsingColorMode=true]
         * if false, this plugin will not use <ColorModeProvider />
         */
        isUsingColorMode: true,
      },
    },
  ],
};
