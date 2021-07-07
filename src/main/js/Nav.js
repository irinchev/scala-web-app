import React from "react";

function Nav() {
    return (
        <nav className="bg-white shadow">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-16">
                    <div className="flex">
                        <div className="-ml-2 mr-2 flex items-center md:hidden">
                            <button type="button"
                                    className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-indigo-500"
                                    aria-controls="mobile-menu" aria-expanded="false">
                                <span className="sr-only">Open main menu</span>
                            </button>
                        </div>
                        <div className="flex-shrink-0 flex items-center">
                            <img className="block lg:hidden h-8 w-auto"
                                 src="https://tailwindui.com/img/logos/workflow-mark-indigo-600.svg" alt="Workflow"/>
                            <img className="hidden lg:block h-8 w-auto"
                                 src="https://tailwindui.com/img/logos/workflow-logo-indigo-600-mark-gray-800-text.svg"
                                 alt="Workflow"/>
                        </div>
                        <div className="hidden md:ml-6 md:flex md:space-x-8">
                            <a href="#"
                               className="border-indigo-500 text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                Dashboard
                            </a>
                            <a href="#"
                               className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                Team
                            </a>
                            <a href="#"
                               className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                Projects
                            </a>
                            <a href="#"
                               className="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                                Calendar
                            </a>
                        </div>
                    </div>
                    <div className="flex items-center">
                        <div className="flex-shrink-0">
                            <button type="button"
                                    className="relative inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                                <span>New Job</span>
                            </button>
                        </div>
                        <div className="hidden md:ml-4 md:flex-shrink-0 md:flex md:items-center">
                            <button
                                className="bg-white p-1 rounded-full text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
                                <span className="sr-only">View notifications</span>
                            </button>
                            <div className="ml-3 relative">
                                <div>
                                    <button type="button"
                                            className="bg-white rounded-full flex text-sm focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                                            id="user-menu-button" aria-expanded="false" aria-haspopup="true">
                                        <span className="sr-only">Open user menu</span>
                                        <img className="h-8 w-8 rounded-full"
                                             src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80"
                                             alt=""/>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    );
}

export default Nav;