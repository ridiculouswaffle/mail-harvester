# Mail Harvester

> [!NOTE]
> This project is still under active development. While the latest release is ready to use, it may not be feature complete for some.

A Clojure app that scrapes emails and links from any website. Mail Harvester is:
* Free and Open Source
* Cross-platform
* And requires no programming knowledge to use

Browsers supported:
* Chrome
* Firefox
* Safari

## How to use?

### Setup for Safari
> [!NOTE]
> If you are using Chrome or Firefox, skip this section.

If you want to use Safari, you should enable a feature to use this application (Remote Automation)

For Sonoma:
* Open Safari > Preferences from the menu bar (or use the shortcut Command + ,)
* Go to the Advanced section
* Check the "Show features for web developers" checkbox
* Go to the Developer section
* Check the "Allow Remote Automation" checkbox

For Ventura and below:
* Open Safari > Preferences from the menu bar (or use the shortcut Command + ,)
* Go to the Advanced Section
* Check the "Show Develop menu in menu bar"
* Click Safari > Develop > Allow Remote Automation from the menu bar

### Prerequisites & Installation

Before using this app, you need to install Java from [here](https://adoptium.net).

After you have installed Java, download the latest release from the Releases section in the right side of this page

After you have download the archive, unzip it and double click the .jar to use!

## Planned features

- [ ] Go through a list of links to scrape more links/emails
- [ ] Filters
- [ ] Notifying the user when scraping is done
- [x] Dark mode

## For Developers

### Setup for development

While running locally, this project expects the drivers to be in a folder named `drivers`. For users convenience, these drivers are packaged in the Releases, but not in the repository.

If you run it with the `clj` tool, they should be in the root of the repository. If you run it after compiling it in a jar, it needs to be in the `target` directory (or wherever the JAR is)

The browsers this application supports are:
* Chrome
* Firefox
* Safari

You can download the drivers for them at:
* [Chrome](https://chromedriver.chromium.org/downloads)
* [Firefox](https://github.com/mozilla/geckodriver/releases)
* Safari doesn't need a driver. Check the [note](#note) above

### How to run

To run from the `clj` tool, use `clj -M -m mail-harvester.core`
To compile a JAR, use `clj -T:build uber`

# License

This project is licensed under the MIT License.
